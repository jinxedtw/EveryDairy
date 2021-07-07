package com.tw.longerrelationship.views.activity

import android.app.AlertDialog
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : BaseActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private var isFold: Boolean = true
    private lateinit var dairyAdapter: DairyAdapter

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
        isFold = sharedPreferences.getBoolean(DAIRY_SHOW_FOLD, true).apply {
            isFold = this
            dairyAdapter = DairyAdapter(this@MainActivity, if (this) 1 else 2)
            changeDairyShowTypeIcon()
        }
        sharedPreferences.getInt(ACCOUNT_SEX, 0).apply {
            changeHeadImage(this)
        }

        mBinding.includeMain.rvDairy.adapter = dairyAdapter
        changeRecyclerView()

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
                    isFold = !isFold
                    changeRecyclerView()
                    changeDairyShowTypeIcon()
                    sharedPreferences.edit().putBoolean(DAIRY_SHOW_FOLD, isFold).apply()
                    dairyAdapter.notifyDataSetChanged()
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
                            changeHeadImage(which)
                            sharedPreferences.edit().putInt(ACCOUNT_SEX, which).apply()
                            dialog.cancel()
                        }.show()
                }
            }
        }
    }

    private fun changeRecyclerView() {
        if (isFold) {
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
        // TODO 如何监听返回的数据量,修改UI
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getAllDairy().collect {
                dairyAdapter.submitData(it)
            }
        }
    }

    /**
     * 修改drawer的头部图片
     * 0 man
     * 1 woman
     */
    private fun changeHeadImage(type: Int) {
        when (type) {
            0 -> {
                mBinding.navigation.getHeaderView(0)
                    .findViewById<ImageView>(R.id.iv_head).setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.ic_boy
                        )
                    )
            }
            1 -> {
                mBinding.navigation.getHeaderView(0)
                    .findViewById<ImageView>(R.id.iv_head).setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.ic_girl
                        )
                    )
            }
        }
    }

    /**
     * 修改日记列表的展示方式的图标
     */
    private fun changeDairyShowTypeIcon() {
        if (isFold) {
            mBinding.includeMain.includeBar.ivDisplay.setImageDrawable(
                ContextCompat.getDrawable(
                    baseContext,
                    R.drawable.pic_unfold,
                )
            )
        } else {
            mBinding.includeMain.includeBar.ivDisplay.setImageDrawable(
                ContextCompat.getDrawable(
                    baseContext,
                    R.drawable.pic_fold,
                )
            )
        }
    }


    companion object {
        const val DAIRY_SHOW_FOLD = "dairyShowFold"     // 打开日记的方式
        const val ACCOUNT_SEX = "sex"                   // 性别
    }
}


