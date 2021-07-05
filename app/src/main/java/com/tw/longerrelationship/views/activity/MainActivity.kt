package com.tw.longerrelationship.views.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.DairyAdapter
import com.tw.longerrelationship.databinding.ActivityMainBinding
import com.tw.longerrelationship.util.*

import com.tw.longerrelationship.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : BaseActivity() {
    private lateinit var mBinding: ActivityMainBinding

    private val sharedPreferences
        get() = baseContext.getSharedPreferences(
            Constants.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

    private val dairyType
        get() = sharedPreferences.getInt(DAIRY_SHOW_TYPE, 1)


    private var dairyAdapter: DairyAdapter = DairyAdapter(this)

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtils.getMainViewModelFactory()
        ).get(MainViewModel::class.java)
    }

    override fun init() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())

        requestSDCardWritePermission(this)
        getDairyData()
        initView()
    }

    private fun initView() {
        mBinding.includeMain.rvDairy.adapter = dairyAdapter
        changeRecyclerView(dairyType)

        mBinding.includeMain.smartRefresh.apply {
            isEnableLoadmore = false    //是否启用上拉加载功能

            setOnRefreshListener {      // 设置下拉刷新
                finishRefresh()
                getDairyData()
            }
        }

        mBinding.navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.it_pictures -> showToast(this, "我点了图库")
                R.id.it_about -> showToast(this, "我点了关于")
            }
            return@setNavigationItemSelectedListener true
        }

        // 设置点击事件
        setOnClickListeners(
            mBinding.includeMain.includeBar.ivMine,
            mBinding.includeMain.includeBar.ivDisplay,
            mBinding.includeMain.fbEdit,
            mBinding.includeMain.includeBar.llSearch,
            mBinding.navigation.getHeaderView(0)
        ) {
            when (this) {
                mBinding.includeMain.includeBar.ivMine -> {
                    mBinding.drawerLayout.openDrawer(GravityCompat.START)
                }
                mBinding.includeMain.includeBar.ivDisplay -> {
                    if (viewModel.isFold) {
                        mBinding.includeMain.includeBar.ivDisplay.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.pic_unfold,
                            )
                        )
                        viewModel.isFold = false
                        changeRecyclerView(2)
                        dairyAdapter.notifyDataSetChanged()
                    } else {
                        mBinding.includeMain.includeBar.ivDisplay.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.pic_fold,
                            )
                        )
                        viewModel.isFold = true
                        changeRecyclerView(1)
                        dairyAdapter.notifyDataSetChanged()
                    }
                }
                mBinding.includeMain.fbEdit -> {
                    startActivity(Intent(this.context, DairyEditActivity::class.java))
                }
                mBinding.includeMain.includeBar.llSearch -> {
                    startActivity(Intent(this.context, SearchActivity::class.java))
                }
                // 点击navigation头部
                mBinding.navigation.getHeaderView(0) -> {
                    AlertDialog.Builder(this@MainActivity)
                        .setItems(
                            arrayOf("男", "女")
                        ) { dialog, which ->
                            if (which == 0) {
                                mBinding.navigation.getHeaderView(0)
                                    .findViewById<ImageView>(R.id.iv_head).setImageDrawable(
                                        ContextCompat.getDrawable(
                                            this@MainActivity,
                                            R.drawable.ic_boy
                                        )
                                    )
                            } else {
                                mBinding.navigation.getHeaderView(0)
                                    .findViewById<ImageView>(R.id.iv_head).setImageDrawable(
                                        ContextCompat.getDrawable(
                                            this@MainActivity,
                                            R.drawable.ic_girl
                                        )
                                    )
                            }
                            dialog.cancel()
                        }.show()
                }
            }
        }
    }

    private fun changeRecyclerView(dairyType: Int) {
        if (dairyType == 1) {
            mBinding.includeMain.rvDairy.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                (adapter as DairyAdapter).type = 1
            }
        } else {
            mBinding.includeMain.rvDairy.apply {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                (adapter as DairyAdapter).type = 2
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    private fun getDairyData() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getAllDairy().collect {
                dairyAdapter.submitData(it)
            }
        }
    }

    override fun onDestroy() {
        // TODO 这里换成workManage
//        sharedPreferences.edit().apply {
//            putInt(DAIRY_SHOW_TYPE, if (viewModel.isFold) 1 else 2)
//            apply()
//        }
        super.onDestroy()
    }

    companion object {
        const val DAIRY_SHOW_TYPE = "dairyShowType"     // 打开日记的方式
        const val ACCOUNT_SEX = "sex"                   // 性别
    }
}


