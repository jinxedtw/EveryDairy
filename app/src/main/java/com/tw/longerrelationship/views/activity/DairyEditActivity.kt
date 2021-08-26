package com.tw.longerrelationship.views.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.tw.longerrelationship.MyApplication
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.PictureSelectAdapter
import com.tw.longerrelationship.databinding.ActivityDairyEditBinding
import com.tw.longerrelationship.help.LocationService
import com.tw.longerrelationship.help.SpacesItemDecoration
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.viewmodel.DairyEditViewModel
import com.tw.longerrelationship.views.widgets.ColorsPainDialog
import com.tw.longerrelationship.views.widgets.IconSelectDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


/**
 * TODO 意外退出时的恢复上次编辑功能
 */
class DairyEditActivity : BaseActivity() {
    private var showRvPhotoList: Boolean = true

    private lateinit var mBinding: ActivityDairyEditBinding
    private lateinit var locationService: LocationService
    private lateinit var locationListener: BDAbstractLocationListener
    private lateinit var pictureSelectAdapter: PictureSelectAdapter
    private val layoutManager = GridLayoutManager(this, 3)

    private val dairyId by lazy {
        intent.getIntExtra(DAIRY_ID, -1)
    }
    private val moodDialog: IconSelectDialog by lazy {
        IconSelectDialog(this, R.style.Dialog, 2) { drawable, iconId ->
            mBinding.ivMood.setImageDrawable(drawable)
            viewModel.moodIcon = iconId
        }
    }
    private val weatherDialog: IconSelectDialog by lazy {
        IconSelectDialog(this, R.style.Dialog, 1) { drawable, iconId ->
            mBinding.ivWeather.setImageDrawable(drawable)
            viewModel.weatherIcon = iconId
        }
    }

    /**
     * 记录相机保存图片的Uri
     */
    private lateinit var currentUri: Uri

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtils.getDairyEditViewModelFactory(dairyId)
        ).get(DairyEditViewModel::class.java)
    }

    /**
     * 初始化相册启动器
     */
    private val toAlbumLauncher =
        registerForActivityResult(ToSystemAlbumResultContract()) {
            if (it != null) {
                viewModel.pictureList.add(it)
                viewModel.isChanged.value = true
                // 只需要刷新新增的一个和尾部,也是就itemCount为2
                pictureSelectAdapter.notifyItemRangeChanged(viewModel.pictureList.size, 2)
            }
        }

    /**
     * 初始化相机启动器
     */
    private val toCameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                viewModel.pictureList.add(currentUri)
                viewModel.isChanged.value = true
                pictureSelectAdapter.notifyItemRangeChanged(viewModel.pictureList.size, 2)
            }
        }

    /**
     * 跳转Activity启动器
     */
    private val toPictureInfoLauncher = registerForActivityResult(ToPictureInfoResultContract()) {
        if (it != -1) {
            viewModel.pictureList.removeAt(it)
            if (viewModel.pictureList.size == 0) viewModel.isChanged.value = false
            // 刷新部分item
            pictureSelectAdapter.notifyItemRemoved(it)

            for (i in it until layoutManager.childCount - 2) {
                val imageView = layoutManager.getChildAt(i + 1) as ImageView
                imageView.tag = i
            }
        }
    }

    /**
     * 下面两个方法用于监听触摸事件和软件盘输入事件,并尝试关闭已经显示的dialog
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev!!.action == MotionEvent.ACTION_DOWN) tryHideDialog()
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        tryHideDialog()
        return super.dispatchKeyEvent(event)
    }

    private fun tryHideDialog() {
        if (moodDialog.isShowing || weatherDialog.isShowing) {
            if (moodDialog.isShowing) {
                moodDialog.cancel()
            } else {
                weatherDialog.cancel()
            }
        }
    }

    override fun init(): View {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mBinding.viewModel = this.viewModel
        mBinding.etContent.requestFocus()
        observe()
        initView()
        addOnSoftKeyBoardVisibleListener()
        initEditText()
        initLocation()
        return mBinding.root
    }

    private fun observe() {
        // 通过监控文本状态,改变标题栏图标
        viewModel.isChanged.observe(this) {
            if (isDiaryNotChanged()) {
                mBinding.appBar.leftIcon.setDrawable(R.drawable.ic_close)
                mBinding.appBar.tag = "noChanged"
            } else {
                mBinding.appBar.leftIcon.setDrawable(R.drawable.ic_select)
                mBinding.appBar.tag = "changed"         // 设置一个标记
            }
        }

        // 监控日记内容,显示字数
        viewModel.dairyContent.observe(this) {
            mBinding.tvTextLength.text =
                String.format(getString(R.string.text_content_num), it.length)
        }

        // 获取日记成功时的回调,这里回调代表是编辑日记状态
        viewModel.dairyItem.observe(this) {
            mBinding.viewModel = viewModel
            if (it.content != null) {
                mBinding.etContent.setText(it.content)
                mBinding.etContent.setSelection(it.content.length)
            }
            if (it.title != null) {
                mBinding.appBar.setTitle(it.title)
            }
            viewModel.apply {
                location = it.location
                pictureList = it.uriList as ArrayList<Uri>
                createTime = it.createTime
                editInfoList = it.editInfoList
                weatherIcon = it.weather
                moodIcon = it.mood
                ifLove = it.isLove
            }
            pictureSelectAdapter.pictureList = viewModel.pictureList
            pictureSelectAdapter.notifyDataSetChanged()
        }
    }

    /**
     * 初始化View,监控ViewModel,设置点击事件
     */
    @SuppressLint("SetTextI18n")
    private fun initView() {
        pictureSelectAdapter = PictureSelectAdapter(viewModel.pictureList, this)

        mBinding.rvPhotoList.apply {
            addItemDecoration(SpacesItemDecoration(60))
            adapter = pictureSelectAdapter
            layoutManager = this@DairyEditActivity.layoutManager
        }

        setOnClickListeners(
            mBinding.ivRecording,
            mBinding.ivCalendar,
            mBinding.ivClock,
            mBinding.ivLocation,
            mBinding.ivWeather,
            mBinding.ivPainting,
            mBinding.ivMood
        ) {
            when (this) {
                mBinding.ivRecording -> {
                    openRecording()
                }
                mBinding.ivCalendar -> {

                }
                // 添加时间
                mBinding.ivClock -> {
                    mBinding.etContent.text =
                        mBinding.etContent.text.append("[" + getNowTimeHour(Date()) + "]")
                    mBinding.etContent.setSelection(mBinding.etContent.text.length)
                }
                // 获得位置
                mBinding.ivLocation -> {
                    requestPositioningPermission(context as Activity)
                    if (locationService.isStart)
                        locationService.reStart()
                    locationService.start()
                }
                // 设置心情
                mBinding.ivMood -> {
                    moodDialog.show()
                }
                // 设置天气
                mBinding.ivWeather -> {
                    weatherDialog.show()
                }
                mBinding.ivPainting -> {
                    showColorsDialog()
                }
            }
        }
    }

    /**
     * 配置定位信息
     */
    private fun initLocation() {
        locationListener = object : BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation?) {
                if (location?.locType ?: 0 == 62) {
                    showToast(baseContext, "定位失败,请检查定位系统是否已经开启")
                    mBinding.tvLocationInfo.text = "无位置信息"
                } else {
                    mBinding.tvLocationInfo.text = location?.addrStr
                }
            }
        }
        locationService = MyApplication.locationService
        locationService.registerListener(locationListener)
        locationService.setLocationOption(locationService.defaultLocationClientOption)
    }

    /**
     * 初始化监听输入框
     */
    private fun initEditText() {
        mBinding.etContent.doOnTextChanged { text, _, _, _ ->
            if (!TextUtils.isEmpty(text))
                viewModel.isChanged.postValue(true)
            else
                viewModel.isChanged.postValue(false)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_dairy_edit
    }

    /**
     * 监听软键盘状态
     */
    private fun addOnSoftKeyBoardVisibleListener() {
        val decorView: View = this.window.decorView
        decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            decorView.getWindowVisibleDisplayFrame(rect)
            // 如果decorView的高度小于原来的80%就说明弹出了软键盘
            if ((rect.bottom - rect.top).toDouble() / decorView.height < 0.8) {
                mBinding.rvPhotoList.visibility = View.GONE
                mBinding.etContent.requestFocus()
                decorView.handler.postDelayed({
                    mBinding.llTextAndCount.visibility = View.VISIBLE
                }, 50)
            } else {
                mBinding.llTextAndCount.visibility = View.GONE
                if (showRvPhotoList) mBinding.rvPhotoList.visibility =
                    View.VISIBLE else mBinding.rvPhotoList.visibility = View.GONE
            }
        }
    }

    /**
     * 保存日记
     */
    fun saveDairy() {
        lifecycleScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                viewModel.saveDairy(mBinding.appBar.getTitle())
            }
            if (result.isSuccess)
                showToast(baseContext, "保存成功")
            else
                showToast(baseContext, "保存失败")
        }
        finishActivity()
    }

    fun finishActivity() {
        if (isSoftShowing()) {
            closeKeyboard(mBinding.root.windowToken)
            showRvPhotoList = false
            mBinding.root.handler.postDelayed({
                finish()
            }, 50)
        } else {
            finish()
        }
    }

    private fun showColorsDialog() {
        ColorsPainDialog(this).show(supportFragmentManager, "dialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        locationService.unregisterListener(locationListener) //注销掉监听
        locationService.stop() //停止定位服务
    }

    /**
     * 判断是否修改了日记
     * 标题,图片,文本内容
     */
    private fun isDiaryNotChanged(): Boolean =
        !viewModel.isChanged.value!!
                && TextUtils.isEmpty(viewModel.dairyContent.value)
                && viewModel.pictureList.isEmpty()


    fun openCamera() {
        currentUri = createImageFile(baseContext)
        toCameraLauncher.launch(currentUri)
    }

    fun openAlbum() {
        toAlbumLauncher.launch(null)
    }

    fun closeKeyboard() {
        super.closeKeyboard(mBinding.root.windowToken)
    }

    /**
     * 调用系统录音
     */
    private fun openRecording() {
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        ActivityCompat.startActivityForResult(this, intent, 3, null)
    }

    fun pictureInfoActivityJump(index: Int) {
        val bundle = Bundle().apply {
            putParcelableArrayList(PICTURE_LIST, viewModel.pictureList)
            putInt(CURRENT_PICTURE, index)
        }
        toPictureInfoLauncher.launch(bundle)
    }

    /**
     * 监听回退键,当正在编辑时弹出提示框
     */
    override fun onBackPressed() {
        if (isDiaryNotChanged()) {
            super.onBackPressed()
        } else {
            AlertDialog.Builder(this).setMessage("确定放弃此次编辑?")
                .setNegativeButton("放弃") { _, _ ->
                    super.onBackPressed()
                }
                .setPositiveButton("保存") { _, _ ->
                    saveDairy()
                }
                .setNeutralButton("取消", null)
                .show()
        }
    }

    /**
     * 跳转到[PictureInfoActivity]  协议类
     * 传入参数  ArrayList<Bitmap>  图片列表
     * 返回参数  Int                标识哪一张图片被删除
     */
    inner class ToPictureInfoResultContract : ActivityResultContract<Bundle, Int>() {
        override fun createIntent(context: Context, input: Bundle): Intent {
            return Intent(context, PictureInfoActivity::class.java).apply {
                putExtras(input)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Int {
            val data = intent?.getIntExtra("result", -1)
            return if (resultCode == Activity.RESULT_OK && data != null) data else -1
        }
    }

    /**
     * 跳转到系统相册
     * 传入参数  ArrayList<Bitmap>  图片列表
     * 返回参数  Int                标识哪一张图片被删除
     */
    inner class ToSystemAlbumResultContract : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit?): Intent {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            intent.type = "image/*"
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }
    }

    // dev 1
}