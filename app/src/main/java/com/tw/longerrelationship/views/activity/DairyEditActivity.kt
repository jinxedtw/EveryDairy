package com.tw.longerrelationship.views.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.ColorUtils
import androidx.core.widget.doOnTextChanged
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
import com.tw.longerrelationship.util.Constants.INTENT_CURRENT_PICTURE
import com.tw.longerrelationship.util.Constants.INTENT_DAIRY_ID
import com.tw.longerrelationship.util.Constants.INTENT_PICTURE_LIST
import com.tw.longerrelationship.util.Constants.KEY_RECOVER_CONTENT
import com.tw.longerrelationship.util.Constants.KEY_RECOVER_TITLE
import com.tw.longerrelationship.viewmodel.DairyEditViewModel
import com.tw.longerrelationship.views.widgets.ColorsPainDialog
import com.tw.longerrelationship.views.widgets.ColorsPainDialog.Companion.DEFAULT_COLOR_INDEX
import com.tw.longerrelationship.views.widgets.ColorsPainDialog.Companion.colorList
import com.tw.longerrelationship.views.widgets.IconSelectDialog
import com.tw.longerrelationship.views.widgets.ToastWithImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class DairyEditActivity : BaseActivity<ActivityDairyEditBinding>() {
    private var showRvPhotoList: Boolean = true
    private var isNeedToSaved: Boolean = true        // 日记是否需要保存
    private var recoveredTitle: String? = null      // 恢复日记标题
    private var recoveredContent: String? = null    // 恢复日记内容

    private lateinit var ripperDrawable: RippleDrawable
    private lateinit var locationService: LocationService
    private lateinit var locationListener: BDAbstractLocationListener
    private lateinit var pictureSelectAdapter: PictureSelectAdapter
    private val layoutManager = GridLayoutManager(this, 3)

    private val dairyId by lazy {
        intent.getIntExtra(INTENT_DAIRY_ID, -1)
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

    /**
     * 记录相机保存的图片
     */
    private lateinit var pictureFile: File

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
                galleryAddPic()
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

    override fun init() {
        initBinding()
        mBinding.viewModel = this.viewModel
        mBinding.etContent.requestFocus()
        observe()
        initTheme()
        initView()
        addOnSoftKeyBoardVisibleListener()
        initEditText()
        initLocation()
    }

    private fun initTheme() {
        setThemeBackGround(colorList[DataStoreUtil.getSyncData(DEFAULT_COLOR_INDEX, 0)?:0])
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
        pictureSelectAdapter = PictureSelectAdapter(viewModel.pictureList, this, ripperDrawable)

        tryToRecoverDairy()

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
            mBinding.ivMood,
            mBinding.clRecover
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
                mBinding.clRecover -> {
                    AlertDialog.Builder(this@DairyEditActivity).setMessage("是否恢复上次未保存内容?")
                        .setNegativeButton("放弃") { _, _ ->
                            lifecycleScope.launch {
                                DataStoreUtil.removeData(KEY_RECOVER_CONTENT, "")
                                DataStoreUtil.removeData(KEY_RECOVER_TITLE, "")
                            }
                            mBinding.clRecover.gone()
                        }
                        .setPositiveButton("恢复") { _, _ ->
                            lifecycleScope.launch {
                                DataStoreUtil.removeData(KEY_RECOVER_CONTENT, "")
                                DataStoreUtil.removeData(KEY_RECOVER_TITLE, "")
                            }
                            mBinding.clRecover.gone()
                            recoverDairy()
                        }
                        .setNeutralButton("取消", null)
                        .show()
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
                    mBinding.tvLocationInfo.text = "定位失败,请检查定位系统是否已经开启"
                } else {
                    mBinding.tvLocationInfo.text = location?.addrStr
                }
            }
        }
        locationService = LocationService(MyApplication.appContext)
        locationService.registerListener(locationListener)
        locationService.setLocationOption(locationService.defaultLocationClientOption)
        locationService.start()
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
        lifecycleScope.launch(Dispatchers.IO) {
//            for (i in 1..100){
                val result = viewModel.saveDairy(mBinding.appBar.getTitle())
                if (result.isSuccess) {
                    isNeedToSaved = false
                    DataStoreUtil.removeData(KEY_RECOVER_CONTENT, "")
                    DataStoreUtil.removeData(KEY_RECOVER_TITLE, "")
                    runOnUiThread { ToastWithImage.showToast("保存成功", true) }
                    finishActivity()
                } else {
                    runOnUiThread { ToastWithImage.showToast("保存失败", false) }
                }
//            }
        }
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
        ColorsPainDialog(this) { colorRes ->
            setThemeBackGround(colorRes)
            pictureSelectAdapter.rippleDrawable = this.ripperDrawable
            pictureSelectAdapter.notifyItemChanged(pictureSelectAdapter.itemCount - 1)
        }.show(supportFragmentManager, "dialog")
    }

    private fun tryToRecoverDairy() {
        lifecycleScope.launch(Dispatchers.Main) {
            DataStoreUtil.readStringFlow(KEY_RECOVER_CONTENT).first {
                if (it.isNotEmpty()) {
                    recoveredContent = it
                }
                true
            }
            DataStoreUtil.readStringFlow(KEY_RECOVER_TITLE).first {
                if (it.isNotEmpty()) {
                    recoveredTitle = it
                }
                true
            }

            if (recoveredTitle != null) {
                mBinding.clRecover.visible()
                mBinding.tvRecoverTitle.text = recoveredTitle
            } else {
                mBinding.tvRecoverTitle.gone()
            }

            if (recoveredContent != null) {
                mBinding.clRecover.visible()
                mBinding.tvRecoverContent.text = recoveredContent
            } else {
                mBinding.tvRecoverContent.gone()
            }
        }
    }

    private fun recoverDairy() {
        if (recoveredContent != null) {
            mBinding.etContent.setText(recoveredContent!!)
            mBinding.etContent.setSelection(recoveredContent!!.length)
        }

        if (recoveredTitle != null) {
            mBinding.appBar.setTitle(recoveredTitle!!)
        }
    }

    override fun onPause() {
        super.onPause()
        if (!TextUtils.isEmpty(viewModel.dairyContent.value) && isNeedToSaved) {
            DataStoreUtil.saveSyncStringData(KEY_RECOVER_CONTENT, viewModel.dairyContent.value!!)
            DataStoreUtil.saveSyncStringData(KEY_RECOVER_TITLE, mBinding.appBar.getTitle())
        }
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
        pictureFile = createImageFile()
        currentUri = FileProvider.getUriForFile(
            this,
            "com.example.android.fileprovider",
            pictureFile
        )
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
            putParcelableArrayList(INTENT_PICTURE_LIST, viewModel.pictureList)
            putInt(INTENT_CURRENT_PICTURE, index)
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
                    isNeedToSaved = false
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
     * 设置日记编辑的背景主题
     * 判断颜色是否是亮色,设置对应主题字体
     *
     * todo 自定义背景功能
     */
    private fun setThemeBackGround(@ColorRes colorRes: Int) {
        val color: Int = ContextCompat.getColor(this, colorRes)
        mBinding.clEditDairy.setBackgroundResource(colorRes)
        setStatusBarColor(color)

        if (ColorUtils.calculateLuminance(color) > 0.6) {
            // 亮色
            setAndroidNativeLightStatusBar(this, true)
        } else {
            // 暗色
            setAndroidNativeLightStatusBar(this, false)
        }

        val shape = GradientDrawable()
        shape.color = ColorStateList.valueOf(addColorDepth(color))
        shape.cornerRadius = dp2px(5f)

        ripperDrawable =
            RippleDrawable(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.DairyEditHintText)), shape, null)
    }

    /**
     * 将原有颜色的R,G,B值依次减去20得到一个更深的颜色
     */
    private fun addColorDepth(color: Int): Int {
        var red = color and 0xff0000 shr 16
        var green = color and 0x00ff00 shr 8
        var blue = color and 0x0000ff

        red = if (red - 25 > 0) red - 25 else 0
        green = if (green - 25 > 0) green - 25 else 0
        blue = if (blue - 25 > 0) blue - 25 else 0
        return Color.rgb(red, green, blue)
    }

    /**
     * 把照片添加到图库
     */
    private fun galleryAddPic() {
        // 保存图片
        MediaStore.Images.Media.insertImage(contentResolver, pictureFile.toString(), "title", "description")
        // 更新图库
        MediaScannerConnection.scanFile(baseContext, arrayOf(pictureFile.toString()), null, null)
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
}