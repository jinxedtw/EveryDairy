package com.tw.longerrelationship.views.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.FragmentAdapter
import com.tw.longerrelationship.databinding.ActivityMainBinding
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.viewmodel.MainViewModel
import com.tw.longerrelationship.views.fragment.BaseFragment
import com.tw.longerrelationship.views.fragment.NoteFragment
import com.tw.longerrelationship.views.fragment.ToDoFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class HomeActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var tapTitle: List<String>
    private lateinit var fragments: List<BaseFragment>
    private lateinit var toDoFragment: ToDoFragment
    private lateinit var noteFragment: NoteFragment

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtils.getMainViewModelFactory()
        ).get(MainViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        setFlBtAnim()
    }

    override fun init() {
        requestSDCardWritePermission(this)
        initBinding()
        initTab()
        initView()
        observe()
    }

    private fun observe() {
        viewModel.isFold.observe(this) {
            changeDairyShowTypeIcon()
        }
        viewModel.ifEnterCheckBoxType.observe(this) {
            enterOrExitCheckBoxType(it)
        }
    }

    private fun initTab() {
        // TODO: 2021/7/25 切换tab卡顿
        noteFragment = NoteFragment()
        toDoFragment = ToDoFragment()
        tapTitle = arrayListOf("笔记", "待办")
        fragments = arrayListOf(noteFragment, toDoFragment)

        mBinding.includeMain.vpMain.apply {
            adapter = FragmentAdapter(fragments, this@HomeActivity)
        }
        //TabLayout和ViewPager的绑定
        TabLayoutMediator(
            mBinding.includeMain.tabLayout, mBinding.includeMain.vpMain
        ) { tab, position ->
            tab.text = tapTitle[position]
        }.attach()

        // 滑动监听
        mBinding.includeMain.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.text == tapTitle[0]) {
                    viewModel.tabSelect = 0
                    mBinding.includeMain.includeBar.ivDisplay.visibility = View.VISIBLE
                    mBinding.includeMain.includeBar.tvFilter.visibility = View.GONE
                } else {
                    viewModel.tabSelect = 1
                    mBinding.includeMain.includeBar.ivDisplay.visibility = View.GONE
                    mBinding.includeMain.includeBar.tvFilter.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 未选择时触发
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 选中之后再次点击即复选时触发
            }
        })
    }

    private fun initView() {
        viewModel.isFold.value = dairyShowFold

        changeHeadImage(accountSex)

        mBinding.navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.it_pictures -> showToast(this, "我点了图库")
                R.id.it_about -> showToast(this, "我点了关于")
                R.id.it_secret -> startActivity(Intent(this, SecretActivity::class.java))
            }
            return@setNavigationItemSelectedListener true
        }

        // 设置点击事件
        setOnClickListeners(
            mBinding.includeMain.includeBar.ivMine,
            mBinding.includeMain.includeBar.ivDisplay,
            mBinding.includeMain.includeBar.llSearch,
            mBinding.includeMain.includeBar.tvFilter,
            mBinding.navigation.getHeaderView(0),
            mBinding.includeMain.fbEdit,
            mBinding.includeMain.includeCheckBar.ivClose,
            mBinding.includeMain.includeCheckBar.tvDelete,
            mBinding.includeMain.includeCheckBar.tvSelectAll
        ) {
            when (this) {
                mBinding.includeMain.includeBar.ivMine -> {
                    mBinding.drawerLayout.openDrawer(GravityCompat.START)
                }
                mBinding.includeMain.includeBar.ivDisplay -> {
                    viewModel.isFold.value = !viewModel.isFold.value!!
                    dairyShowFold = viewModel.isFold.value!!
                }
                mBinding.includeMain.includeBar.llSearch -> {
                    startActivity(Intent(this.context, SearchActivity::class.java))
                    overridePendingTransition(R.anim.animation_left_in, R.anim.animation_right_out)
                }
                // 点击navigation头部
                mBinding.navigation.getHeaderView(0) -> {
                    AlertDialog.Builder(this@HomeActivity)
                        .setItems(
                            arrayOf("男", "女")
                        ) { dialog, which ->
                            changeHeadImage(which)
                            accountSex = which
                            dialog.cancel()
                        }.show()
                }
                mBinding.includeMain.includeCheckBar.ivClose -> {
                    entryCheckType(false)
                }
                mBinding.includeMain.includeCheckBar.tvDelete -> {
                    AlertDialog.Builder(this@HomeActivity).setMessage("确定删除所选笔记吗")
                        .setNegativeButton("取消", null).setPositiveButton("确认") { _, _ ->
                            noteFragment.deleteDairy()
                            entryCheckType(false)
                        }.show()
                }
                mBinding.includeMain.includeCheckBar.tvSelectAll -> {
                    noteFragment.selectAll()
                }
                mBinding.includeMain.fbEdit -> {
                    // 新建笔记或新建待办的入口
                    if (viewModel.tabSelect == 0)
                        startActivity(Intent(this@HomeActivity, DairyEditActivity::class.java))
                    else
                        startActivity(Intent(this@HomeActivity, ToDoEditActivity::class.java))
                }
                mBinding.includeMain.includeBar.tvFilter -> {

                }
            }
        }
    }

    /**
     * 设置浮动按钮的动画
     */
    private fun setFlBtAnim() {
        val animatorSet = AnimatorSet()
        val rotationAnim1 = ObjectAnimator.ofFloat(mBinding.includeMain.fbEdit, "rotationY", 90f, -30f)
        val rotationAnim2 = ObjectAnimator.ofFloat(mBinding.includeMain.fbEdit, "rotationY", -30f, 0f)
        animatorSet.play(rotationAnim1).before(rotationAnim2)
        animatorSet.duration = 500
        animatorSet.start()
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    fun entryCheckType(boolean: Boolean) {
        viewModel.ifEnterCheckBoxType.value = boolean
    }

    /**
     * 进入或退出选择模式
     * @param boolean 是否进入选择模式
     */
    private fun enterOrExitCheckBoxType(boolean: Boolean) {
        if (boolean) {
            mBinding.includeMain.vpMain.isUserInputEnabled = false              // 禁止左右滑动
            mBinding.includeMain.tabLayout.visibility = View.GONE
            mBinding.includeMain.includeBar.root.visibility = View.GONE
            mBinding.includeMain.includeCheckBar.root.visibility = View.VISIBLE
        } else {
            mBinding.includeMain.vpMain.isUserInputEnabled = true
            mBinding.includeMain.includeBar.root.visibility = View.VISIBLE
            mBinding.includeMain.tabLayout.visibility = View.VISIBLE
            mBinding.includeMain.includeCheckBar.root.visibility = View.GONE
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
                    .findViewById<ImageView>(R.id.iv_head).setDrawable(R.drawable.ic_boy)
            }
            1 -> {
                mBinding.navigation.getHeaderView(0)
                    .findViewById<ImageView>(R.id.iv_head).setDrawable(R.drawable.ic_girl)
            }
        }
    }

    /**
     * 修改日记列表的展示方式的图标
     */
    private fun changeDairyShowTypeIcon() {
        if (viewModel.isFold.value!!) {
            mBinding.includeMain.includeBar.ivDisplay.setDrawable(R.drawable.pic_unfold)
        } else {
            mBinding.includeMain.includeBar.ivDisplay.setDrawable(R.drawable.pic_fold)
        }
    }

    /**
     * 设置选中模式的头部数量
     */
    fun setSelectNum(it: Int) {
        mBinding.includeMain.includeCheckBar.tvSelectNum.text =
            String.format(getString(R.string.alreadySelect), it)
    }

    override fun onBackPressed() {
        if (viewModel.ifEnterCheckBoxType.value!!) {
            entryCheckType(false)
        } else {
            super.onBackPressed()
        }
    }

    fun setTodoComplete(id: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.setTodoComplete(id)
        }
    }

    companion object {
        const val DAIRY_SHOW_FOLD = "dairyShowFold"     // 打开日记的方式
        const val ACCOUNT_SEX = "sex"                   // 性别      0:man   1:woman

        var accountSex: Int
            get() = DataStoreUtil.readIntData(ACCOUNT_SEX)
            set(value) {
                CoroutineScope(Dispatchers.Main).launch {
                    DataStoreUtil.saveIntData(ACCOUNT_SEX, value)
                }
            }

        var dairyShowFold: Boolean
            get() = DataStoreUtil.readBooleanData(DAIRY_SHOW_FOLD, true)
            set(value) {
                CoroutineScope(Dispatchers.Main).launch {
                    DataStoreUtil.saveBooleanData(DAIRY_SHOW_FOLD, value)
                }
            }
    }
}


