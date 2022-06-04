package com.tw.longerrelationship.views.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.blankj.utilcode.util.TimeUtils
import com.tw.longerrelationship.MyApplication
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.PictureSelectAdapter
import com.tw.longerrelationship.databinding.ActivityDairyEditBinding
import com.tw.longerrelationship.help.DairyColorHelper
import com.tw.longerrelationship.help.LocationService
import com.tw.longerrelationship.logic.LiveDataBus
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.util.Constants.INTENT_ALBUM_SELECT_IMAGES
import com.tw.longerrelationship.util.Constants.INTENT_ALBUM_SELECT_NUM
import com.tw.longerrelationship.util.Constants.INTENT_CURRENT_PICTURE
import com.tw.longerrelationship.util.Constants.INTENT_DAIRY_ID
import com.tw.longerrelationship.util.Constants.INTENT_PICTURE_LIST
import com.tw.longerrelationship.util.Constants.KEY_RECOVER_CONTENT
import com.tw.longerrelationship.util.Constants.KEY_RECOVER_TITLE
import com.tw.longerrelationship.viewmodel.DairyEditViewModel
import com.tw.longerrelationship.views.widgets.ColorsPainDialog
import com.tw.longerrelationship.views.widgets.IconSelectDialog
import com.tw.longerrelationship.views.widgets.RecordAudioDialogFragment
import com.tw.longerrelationship.views.widgets.ToastWithImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class DairyEditActivity : BaseActivity<ActivityDairyEditBinding>() {
    private var showRecordBar: Boolean = false
    private var isNeedToSaved: Boolean = true        // 日记是否需要保存
    private var recoveredTitle: String? = null      // 恢复日记标题
    private var recoveredContent: String? = null    // 恢复日记内容
    private var isFirstOpen: Boolean = true

    private lateinit var imgRipperDrawable: RippleDrawable
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
    private var iconColor: Int = 0
    private var isBoldMode: Boolean = false
    private var isCompleteMode: Boolean = false
    private var isListMode: Boolean = false

    /** 记录相机保存图片的Uri */
    private lateinit var currentUri: Uri

    /** 记录相机保存的图片 */
    private lateinit var pictureFile: File

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            InjectorUtils.getDairyEditViewModelFactory(dairyId)
        ).get(DairyEditViewModel::class.java)
    }

//    /** 初始化相册启动器 */
//    private val toAlbumLauncher =
//        registerForActivityResult(ToSystemAlbumResultContract()) {
//            if (it != null) {
//                viewModel.pictureList.add(it)
//                viewModel.isChanged.value = true
//                // 只需要刷新新增的一个和尾部,也是就itemCount为2
//                pictureSelectAdapter.notifyItemRangeChanged(viewModel.pictureList.size, 2)
//            }
//        }

    private val toPhotoAlbumActivityLauncher =
        registerForActivityResult(ToPhotoAlbumResultContract()) {
            if (it != null) {
                logD("选中的图片", it.toString())
                viewModel.pictureList.addAll(it)
                logD("总共图片", viewModel.pictureList.toString())
                viewModel.isChanged.value = true
                // 只需要刷新新增的一个和尾部,也是就itemCount为2
                pictureSelectAdapter.notifyItemRangeChanged(viewModel.pictureList.size, it.size + 1)
            }
        }

    /** 初始化相机启动器 */
    private val toCameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                viewModel.pictureList.add(currentUri.toString())
                viewModel.isChanged.value = true
                pictureSelectAdapter.notifyItemRangeChanged(viewModel.pictureList.size, 2)
                galleryAddPic()
            }
        }

    /** 跳转Activity启动器 */
    private val toPictureInfoLauncher = registerForActivityResult(ToPictureInfoResultContract()) {
        onImageDelete(it)
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
        requestRecordAudioPermission(this@DairyEditActivity)
        initBinding()
        mBinding.viewModel = this.viewModel
        showKeyboard(mBinding.etContent)
        observe()
        setMainColor(DairyColorHelper.getDairyMainColor())
        initView()
        addOnSoftKeyBoardVisibleListener()
        initEditText()
        initLocation()
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
            if (it.recordPath.isNotEmpty()) {
                mBinding.recodeBar.initMedia(it.recordPath)
            }
            viewModel.apply {
                location = it.location
                pictureList = it.uriList as ArrayList<String>
                createTime = it.createTime
                editInfoList = it.editInfoList
                weatherIcon = it.weather
                moodIcon = it.mood
                ifLove = it.isLove
                if (recodePath.isNotEmpty()){
                    mBinding.recodeBar.initMedia(recodePath)
                    mBinding.recodeBar.visible()
                    showRecordBar = true
                }
                recodePath = it.recordPath
            }
            pictureSelectAdapter.pictureList = viewModel.pictureList
            pictureSelectAdapter.notifyDataSetChanged()
        }

        LiveDataBus.with<String>(Constants.LIVE_SAVE_RECODE).observe(this) {
            if (viewModel.recodePath.isNotEmpty()) {
                deleteFileOrFolder(viewModel.recodePath)
            }

            if (isSoftShowing()) {
                mBinding.recodeBar.visible()
            }
            viewModel.recodePath = it
            showRecordBar = true
            mBinding.recodeBar.initMedia(it)
            viewModel.isChanged.postValue(true)
        }
    }

    /**
     * 初始化View,监控ViewModel,设置点击事件
     */
    @SuppressLint("SetTextI18n")
    private fun initView() {
        pictureSelectAdapter = PictureSelectAdapter(viewModel.pictureList, this, imgRipperDrawable).apply {
            onImageClick = {
                pictureInfoActivityJump(it, it.tag as Int, it.transitionName)
            }
            onDeleteClick = {
                onImageDelete(it)
            }
        }
        pictureSelectAdapter.showDeleteImage()

        tryToRecoverDairy()

        mBinding.rvPhotoList.apply {
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
            mBinding.clRecover,
            mBinding.ivBold,
            mBinding.ivCompleteList,
            mBinding.ivListMode
        ) {
            when (this) {
                mBinding.ivRecording -> {
                    RecordAudioDialogFragment().show(supportFragmentManager, "recordDialog")
                }
                mBinding.ivCalendar -> {
                    mBinding.etContent.text = mBinding.etContent.text.append(TimeUtils.date2String(Date(), "[yyyy-MM-dd HH:mm]"))
                    mBinding.etContent.setSelection(mBinding.etContent.text.length)
                }
                // 添加时间
                mBinding.ivClock -> {
                    mBinding.etContent.text = mBinding.etContent.text.append(TimeUtils.date2String(Date(), "[HH:mm]"))
                    mBinding.etContent.setSelection(mBinding.etContent.text.length)
                }
                // 获得位置
                mBinding.ivLocation -> {
                    if (mBinding.ivLocationSmall.visibility == View.VISIBLE) {
                        mBinding.ivLocationSmall.gone()
                        mBinding.tvLocationInfo.gone()
                    } else {
                        mBinding.ivLocationSmall.visible()
                        mBinding.tvLocationInfo.visible()
                        requestPositioningPermission(context as Activity)
                        if (locationService.isStart)
                            locationService.reStart()
                        locationService.start()
                    }

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
                mBinding.ivBold -> {
//                    if (isBoldMode){
//                        mBinding.etContent.setMiddleWeight()
//                        mBinding.ivBold.setColorFilter(ContextCompat.getColor(this@DairyEditActivity,R.color.DairyEditIcon))
//                    }else{
//                        mBinding.etContent.setTextWeight(1f)
//                        mBinding.ivBold.setColorFilter(iconColor)
//                    }
//                    isBoldMode= !isBoldMode
                }
                mBinding.ivCompleteList -> {
//                    if (isCompleteMode){
//                        mBinding.ivCompleteList.setColorFilter(ContextCompat.getColor(this@DairyEditActivity,R.color.DairyEditIcon))
//                    }else{
//                        mBinding.ivCompleteList.setColorFilter(iconColor)
//                    }
//                    isCompleteMode= !isCompleteMode
//                    mBinding.ivCompleteList.setColorFilter(iconColor)
                }
                mBinding.ivListMode -> {
//                    if (isListMode){
//                        mBinding.ivListMode.setColorFilter(ContextCompat.getColor(this@DairyEditActivity,R.color.DairyEditIcon))
//                    }else{
//                        mBinding.ivListMode.setColorFilter(iconColor)
//                    }
//                    isListMode= !isListMode
//                    mBinding.ivListMode.setColorFilter(iconColor)
                }
            }
        }
    }

    /** 删除了选中的图片 */
    private fun onImageDelete(index: Int) {

        if (index != -1) {
            viewModel.pictureList.removeAt(index)
            logD("修改tag", "删除第${index}个")
            logD("修改tag", "此时列表${viewModel.pictureList}")
            if (viewModel.pictureList.size == 0) viewModel.isChanged.value = false
            // 刷新部分item
            pictureSelectAdapter.notifyItemRemoved(index)

            for (i in index until layoutManager.childCount - 2) {
                val imageView = layoutManager.getChildAt(i + 1) as View
                imageView.tag = i
                logD("修改tag", "第${i + 1}个View修改至tag:$i")
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
                    mBinding.tvLocationInfo.text = "无定位信息"
                } else {
                    mBinding.tvLocationInfo.text = location?.addrStr
                }
                // 获取结果后关闭定位,节省电量
                stopLocationService()
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

    /** 监听软键盘状态 */
    private fun addOnSoftKeyBoardVisibleListener() {
        val decorView: View = this.window.decorView
        decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            decorView.getWindowVisibleDisplayFrame(rect)
            // 如果decorView的高度小于原来的80%就说明弹出了软键盘
            if ((rect.bottom - rect.top).toDouble() / decorView.height < 0.8) {
                mBinding.recodeBar.gone()
                mBinding.rvPhotoList.gone()
                decorView.handler.postDelayed({
                    mBinding.llTextAndCount.visibility = View.VISIBLE
                }, 50)
            } else {
                mBinding.llTextAndCount.visibility = View.GONE
                if (showRecordBar) {
                    mBinding.recodeBar.visible()
                }
                mBinding.rvPhotoList.visibility = View.VISIBLE
            }
        }
    }

    /** 保存日记 */
    fun saveDairy() {
        lifecycleScope.launch(Dispatchers.IO) {
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
        }
    }

    fun finishActivity() {
        if (isSoftShowing()) {
            closeKeyboard(mBinding.root.windowToken)
            mBinding.root.handler.postDelayed({
                if (isTaskRoot) {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                finish()
            }, 50)
        } else {
            if (isTaskRoot) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
            finish()
        }
    }

    private fun showColorsDialog() {
        ColorsPainDialog(this) { colorRes ->
            setMainColor(colorRes)
            pictureSelectAdapter.rippleDrawable = this.imgRipperDrawable
            pictureSelectAdapter.notifyItemChanged(pictureSelectAdapter.itemCount - 1)
        }.show(supportFragmentManager, "dialog")
    }

    private fun tryToRecoverDairy() {
        lifecycleScope.launch(Dispatchers.Main) {
            DataStoreUtil.getData(KEY_RECOVER_CONTENT, "").first {
                if (it.isNotEmpty()) {
                    recoveredContent = it
                }
                true
            }
            DataStoreUtil.getData(KEY_RECOVER_TITLE, "").first {
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

    override fun onResume() {
        super.onResume()
        if (isFirstOpen) {
            isFirstOpen = false
        } else {
            mBinding.root.requestFocus()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!TextUtils.isEmpty(viewModel.dairyContent.value) && isNeedToSaved) {
            DataStoreUtil[KEY_RECOVER_CONTENT] = viewModel.dairyContent.value!!
            DataStoreUtil[KEY_RECOVER_TITLE] = mBinding.appBar.getTitle()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationService()
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
        toPhotoAlbumActivityLauncher.launch(viewModel.pictureList.size)
//        toAlbumLauncher.launch(null)
    }

    fun openDoodle() {
        startActivity(Intent(this, DoodleViewActivity::class.java))
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

    private fun pictureInfoActivityJump(view: View, index: Int, transitionId: String) {
        val options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
            this,
            android.util.Pair.create(view, transitionId),
        )
        val bundle = Bundle().apply {
            putStringArrayList(INTENT_PICTURE_LIST, viewModel.pictureList)
            putInt(INTENT_CURRENT_PICTURE, index)
            // 配置过渡元素
            putBundle(ActivityResultContracts.StartActivityForResult.EXTRA_ACTIVITY_OPTIONS_BUNDLE, options.toBundle())
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
    private fun setMainColor(@ColorRes colorRes: Int) {
        val mainColor: Int = ContextCompat.getColor(this, colorRes)
        val recoverAndImageColor = ColorStateList.valueOf(DairyColorHelper.getImageSelectorAndRecoverColor(mainColor))
        val textColor = DairyColorHelper.getTextColor(mainColor)
        val editContentColor = DairyColorHelper.getEditContentColor(mainColor)
        iconColor = DairyColorHelper.getIconColor(mainColor)

        // 整体背景颜色
        mBinding.clEditDairy.setBackgroundResource(colorRes)
        // 状态栏颜色
        setStatusBarColor(mainColor)
        if (DairyColorHelper.isDarkTheme(mainColor)) {
            // 亮色
            setAndroidNativeLightStatusBar(this, true)
        } else {
            // 暗色
            setAndroidNativeLightStatusBar(this, false)
        }

        // 设置选择图片背景
        val imgShape = GradientDrawable()
        imgShape.color = recoverAndImageColor
        imgShape.cornerRadius = dp2px(5f)
        imgRipperDrawable = RippleDrawable(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.DairyEditHintText)), imgShape, null)

        // 设置日记恢复背景
        val recoverShape = GradientDrawable()
        recoverShape.color = recoverAndImageColor
        mBinding.clRecover.background = RippleDrawable(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.DairyEditHintText)), recoverShape, null)

        // 设置字体颜色
        textColor.apply {
            mBinding.tvLocationInfo.setTextColor(this)
            mBinding.tvTextLength.setTextColor(this)
            mBinding.tvTimeInfo.setTextColor(this)
            mBinding.ivTextIcon.setColorFilter(this)
            mBinding.ivLocationSmall.setColorFilter(this)
            mBinding.appBar.mTitle.setTextColor(this)
        }
        mBinding.etContent.setTextColor(editContentColor)
        mBinding.etContent.setHintTextColor(editContentColor)
    }

    /** 把照片添加到图库*/
    private fun galleryAddPic() {
        // 保存图片
        MediaStore.Images.Media.insertImage(contentResolver, pictureFile.toString(), "title", "description")
        // 更新图库
        MediaScannerConnection.scanFile(baseContext, arrayOf(pictureFile.toString()), null, null)
    }

    /** 停止定位服务 */
    private fun stopLocationService() {
        locationService.unregisterListener(locationListener)
        locationService.stop()
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

    inner class ToPhotoAlbumResultContract : ActivityResultContract<Int, List<String>?>() {
        override fun createIntent(context: Context, input: Int?): Intent {
            return Intent(context, PhotoAlbumActivity::class.java).apply { putExtra(INTENT_ALBUM_SELECT_NUM, input) }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): List<String>? {
            return intent?.getStringArrayListExtra(INTENT_ALBUM_SELECT_IMAGES)
        }
    }
}