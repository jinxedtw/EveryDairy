package com.tw.longerrelationship.views.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.DrawerItemAdapter
import com.tw.longerrelationship.adapter.DrawerItemAdapter.Companion.DRAWER_ABOUT
import com.tw.longerrelationship.adapter.DrawerItemAdapter.Companion.DRAWER_COUNTDOWN_DAY
import com.tw.longerrelationship.adapter.DrawerItemAdapter.Companion.DRAWER_FAVORITES
import com.tw.longerrelationship.adapter.DrawerItemAdapter.Companion.DRAWER_HELP
import com.tw.longerrelationship.adapter.DrawerItemAdapter.Companion.DRAWER_PICTURE
import com.tw.longerrelationship.adapter.DrawerItemAdapter.Companion.DRAWER_SECRET
import com.tw.longerrelationship.adapter.DrawerItemAdapter.Companion.DRAWER_SETTING
import com.tw.longerrelationship.adapter.FragmentAdapter
import com.tw.longerrelationship.databinding.ActivityMainBinding
import com.tw.longerrelationship.help.UrlMatchHelper
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.util.Constants.KEY_ACCOUNT_SEX
import com.tw.longerrelationship.util.Constants.KEY_DAIRY_SHOW_FOLD
import com.tw.longerrelationship.viewmodel.MainViewModel
import com.tw.longerrelationship.views.fragment.BaseFragment
import com.tw.longerrelationship.views.fragment.DairyFragment
import com.tw.longerrelationship.views.fragment.ToDoFragment
import com.tw.longerrelationship.views.widgets.ClipPopup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var tapTitle: List<String>
    private lateinit var fragments: List<BaseFragment>
    private lateinit var toDoFragment: ToDoFragment
    private lateinit var dairyFragment: DairyFragment
    private lateinit var mDrawerAdapter: DrawerItemAdapter
    private var lastCopyLink = ""
    private val clipPopup: ClipPopup by lazy { ClipPopup(this) }

    private val viewModel by lazy { ViewModelProvider(this, InjectorUtils.getMainViewModelFactory()).get(MainViewModel::class.java) }

    override fun onResume() {
        super.onResume()
        setFlBtAnim()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        getClipboardContent()
    }

    override fun init() {
        requestSDCardWritePermission(this)
        initBinding()
        initTab()
        initView()
        initDrawer()
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
        dairyFragment = DairyFragment()
        toDoFragment = ToDoFragment()
        tapTitle = arrayListOf("笔记", "待办")
        fragments = arrayListOf(dairyFragment, toDoFragment)

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

    @SuppressLint("SetTextI18n")
    private fun initView() {
        viewModel.isFold.value = dairyShowFold
        changeHeadImage(accountSex)

        // 设置点击事件
        setOnClickListeners(
            mBinding.includeMain.includeBar.ivMine,
            mBinding.includeMain.includeBar.ivDisplay,
            mBinding.includeMain.includeBar.llSearch,
            mBinding.includeMain.includeBar.tvFilter,
            mBinding.includeDrawer.ivHead,
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
                    startActivity(
                        Intent(this@HomeActivity, SearchActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(
                            this@HomeActivity,
                            mBinding.includeMain.includeBar.llSearch,
                            getString(R.string.home_activity_share_bar)
                        ).toBundle()
                    )
//                    overridePendingTransition(R.anim.animation_left_in, R.anim.animation_right_out)
                }
                // 点击navigation头部
                mBinding.includeDrawer.ivHead -> {
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
                            dairyFragment.deleteDairy()
                            entryCheckType(false)
                        }.show()
                }
                mBinding.includeMain.includeCheckBar.tvSelectAll -> {
                    dairyFragment.selectAll()
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

        // 获取天气
        lifecycleScope.launch(Dispatchers.Main) {
            runReportCatching {
                val data = viewModel.requestWeather() ?: return@launch
                mBinding.includeDrawer.tvCity.text = data.results.first().location?.name ?: "--"
                mBinding.includeDrawer.tvTemperature.text =
                    "${data.results.first().now?.temperature ?: "--"}°"
                mBinding.includeDrawer.tvWeather.text = data.results.first().now?.text ?: "--"
            }.onFailure {
                showToast("获取天气错误", debugMode = true)
            }
        }
    }

    private fun initDrawer() {
        //传入侧拉栏数据
        val list = listOf(
            DrawerItemAdapter.DrawerLayoutBean(
                R.string.love,
                R.drawable.ic_favorites,
                DRAWER_FAVORITES
            ),
            DrawerItemAdapter.DrawerLayoutBean(
                R.string.countdown_day,
                R.drawable.ic_countdown_day,
                DRAWER_COUNTDOWN_DAY
            ),
            DrawerItemAdapter.DrawerLayoutBean(
                R.string.pictures,
                R.drawable.ic_photo_album,
                DRAWER_PICTURE
            ),
            DrawerItemAdapter.DrawerLayoutBean(
                R.string.secret,
                R.drawable.ic_secret,
                DRAWER_SECRET
            ),
//            DrawerItemAdapter.DrawerLayoutBean(R.string.help, R.drawable.ic_help, DRAWER_HELP),
            DrawerItemAdapter.DrawerLayoutBean(R.string.about, R.drawable.ic_about, DRAWER_ABOUT),
//            DrawerItemAdapter.DrawerLayoutBean(
//                R.string.setting,
//                R.drawable.ic_settings,
//                DRAWER_SETTING
//            ),
        )
        mDrawerAdapter = DrawerItemAdapter(list)
        mBinding.includeDrawer.rvDrawer.layoutManager = LinearLayoutManager(applicationContext)
        mBinding.includeDrawer.rvDrawer.isNestedScrollingEnabled = false
        mBinding.includeDrawer.rvDrawer.adapter = mDrawerAdapter
        mDrawerAdapter.setClickListener(object : DrawerItemAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                when (list[position].type) {
                    DRAWER_PICTURE -> startActivity(
                        Intent(
                            this@HomeActivity,
                            AlbumActivity::class.java
                        )
                    )
                    DRAWER_COUNTDOWN_DAY -> startActivity(
                        Intent(
                            this@HomeActivity,
                            CalendarActivity::class.java
                        )
                    )
                    DRAWER_ABOUT -> startActivity(
                        Intent(
                            this@HomeActivity,
                            AboutActivity::class.java
                        )
                    )
                    DRAWER_SECRET -> startActivity(
                        Intent(
                            this@HomeActivity,
                            SecretActivity::class.java
                        )
                    )
                    DRAWER_FAVORITES -> startActivity(
                        Intent(
                            this@HomeActivity,
                            FavoritesActivity::class.java
                        )
                    )
                    DRAWER_SETTING -> {
                    }
                }
            }
        })
    }

    /**
     * 设置浮动按钮的动画
     */
    private fun setFlBtAnim() {
        val animatorSet = AnimatorSet()
        val rotationAnim1 =
            ObjectAnimator.ofFloat(mBinding.includeMain.fbEdit, "rotationY", 90f, -30f)
        val rotationAnim2 =
            ObjectAnimator.ofFloat(mBinding.includeMain.fbEdit, "rotationY", -30f, 0f)
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
            mBinding.includeMain.tabLayout.gone()
            mBinding.includeMain.includeBar.root.gone()
            mBinding.includeMain.fbEdit.gone()
            mBinding.includeMain.includeCheckBar.root.visible()
        } else {
            mBinding.includeMain.vpMain.isUserInputEnabled = true
            mBinding.includeMain.includeBar.root.visible()
            mBinding.includeMain.tabLayout.visible()
            mBinding.includeMain.includeCheckBar.root.gone()
            mBinding.includeMain.fbEdit.visible()
        }
    }

    /**
     * 修改drawer的头部图片
     * 0 man
     * 1 woman
     */
    private fun changeHeadImage(type: Int) {
        when (type) {
            0 -> mBinding.includeDrawer.ivHead.setDrawable(R.drawable.ic_boy)
            1 -> mBinding.includeDrawer.ivHead.setDrawable(R.drawable.ic_girl)
        }
    }

    /**
     * 修改日记列表的展示方式的图标
     */
    private fun changeDairyShowTypeIcon() {
        if (viewModel.isFold.value!!) {
            mBinding.includeMain.includeBar.ivDisplay.setDrawable(R.drawable.ic_unfold)
        } else {
            mBinding.includeMain.includeBar.ivDisplay.setDrawable(R.drawable.ic_fold)
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

    override fun onStop() {
        super.onStop()
        mBinding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    /** 获取剪切板复制、剪切内容 */
    private fun getClipboardContent() {
        runReportCatching {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (!clipboardManager.hasPrimaryClip()) {
                return
            }

            val data = clipboardManager.primaryClip
            val clipText = data!!.getItemAt(0).text.toString()

            if (lastCopyLink == clipText || TextUtils.isEmpty(clipText)) return

            if (clipText.matches(UrlMatchHelper.URL_REGEX.toRegex())) {
                clipPopup.showPopupWindow(mBinding.includeMain.viewPlaceholder, clipText)
                lastCopyLink = clipText
            }
        }.onFailure {
            showToast("剪切板出错", debugMode = true)
        }
    }

    companion object {
        var accountSex: Int
            get() = DataStoreUtil[KEY_ACCOUNT_SEX] ?: 0
            set(value) {
                CoroutineScope(Dispatchers.Main).launch {
                    DataStoreUtil.putData(KEY_ACCOUNT_SEX, value)
                }
            }

        var dairyShowFold: Boolean
            get() = DataStoreUtil[KEY_DAIRY_SHOW_FOLD] ?: true
            set(value) {
                CoroutineScope(Dispatchers.Main).launch {
                    DataStoreUtil.putData(KEY_DAIRY_SHOW_FOLD, value)
                }
            }
    }
}


