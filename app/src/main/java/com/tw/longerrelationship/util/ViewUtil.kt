package com.tw.longerrelationship.util

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.tw.longerrelationship.BuildConfig
import com.tw.longerrelationship.MyApplication.Companion.appContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * 封装Toast
 */
fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT, debugMode:Boolean = false) {
    if (debugMode){
        if (BuildConfig.DEBUG){
            Toast.makeText(appContext, message, duration).show()
        }
    }else{
        Toast.makeText(appContext, message, duration).show()
    }
}

// StatusBar -------------------------------------------------------------------------------

/** 谷歌原生方式改变状态栏文字颜色 */
fun setAndroidNativeLightStatusBar(activity: Activity, dark: Boolean) {
    val decor = activity.window.decorView
    if (dark) {
        decor.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        decor.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}

/** 设置状态栏颜色 */
fun Activity.setStatusBarColor(@ColorInt statusColor: Int) {
    val window = this.window
    //添加Flag把状态栏设为可绘制模式
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    //设置状态栏为透明
    window.statusBarColor = statusColor
}

/** 隐藏状态栏 */
fun setStatusBarHidden(window: Window, hidden: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        if (hidden) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
        val decorViewGroup = window.decorView as ViewGroup
        val statusBarView = decorViewGroup.findViewWithTag<View>("custom_status_bar_tag")
        if (statusBarView != null) {
            val hiding = statusBarView.isClickable
            if (hiding == hidden) {
                return
            }
            if (hidden) {
                statusBarView.isClickable = true
                val animator = ObjectAnimator.ofFloat(statusBarView, "y", getStatusBarHeight(window.context).toFloat())
                animator.duration = 200
                animator.startDelay = 200
                animator.start()
            } else {
                statusBarView.isClickable = false
                val animator = ObjectAnimator.ofFloat(statusBarView, "y", 0f)
                animator.duration = 200
                animator.start()
            }
        }
    }
}

/** 获取状态栏高度 */
fun getStatusBarHeight(context: Context): Int {
    //获取status_bar_height资源的ID
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        //根据资源ID获取响应的尺寸值
        return context.resources.getDimensionPixelSize(resourceId)
    }
    return -1
}

// Bitmap -------------------------------------------------------------------------------------------------

/**
 * 把原位图转换成圆角的位图
 *
 * 我们先创建一个空的 bitmap，让画布与bitmap关联起来，
 * 然后把这个画布变成带有圆角的画布，再把我们的要的图片
 * 放到我们的画布上
 */
fun bitmapRound(mBitmap: Bitmap, index: Float): Bitmap? {
    val bitmap = Bitmap.createBitmap(mBitmap.width, mBitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    paint.isAntiAlias = true

    //设置矩形大小
    val rect = Rect(0, 0, mBitmap.width, mBitmap.height)
    val rectf = RectF(rect)

    // 相当于清屏
    canvas.drawARGB(0, 0, 0, 0)
    //画圆角
    canvas.drawRoundRect(rectf, index, index, paint)
    // 取两层绘制，显示上层
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    // 把原生的图片放到这个画布上，使之带有画布的效果
    canvas.drawBitmap(mBitmap, rect, rect, paint)
    return bitmap
}

/**
 * 创建用来存储图片的文件，以时间来命名就不会产生命名冲突
 *
 * @return 创建的图片文件的Uri(APP的外部私有目录)
 */
@SuppressLint("SimpleDateFormat")
@Throws(IOException::class)
fun Context.createImageFile(): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}


/**
 * 高效加载图片方法
 *
 * 先获取到目标图片的宽高,根据所需宽高进行尺寸压缩
 * [calculateInSampleSize] 计算压缩比
 */
fun decodeSampledBitmapFromUri(
    context: Context,
    uri: Uri,
    reqWidth: Int,
    reqHeight: Int
): Bitmap? {
    // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeStream(
        context.contentResolver.openInputStream(uri),
        null,
        options
    )
    // 调用上面定义的方法计算inSampleSize值
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
    // 使用获取到的inSampleSize值再次解析图片
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeStream(
        context.contentResolver.openInputStream(uri),
        null,
        options
    )
}

fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int, reqHeight: Int
): Int {
    // 源图片的高度和宽度
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        // 计算出实际宽高和目标宽高的比率
        val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
        val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
        // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
        // 一定都会大于等于目标的宽和高。
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    return inSampleSize
}

/** 弹出动画 */
fun slideToUp(view: View) {
    val slide: Animation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 0.0f,
        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
        1.0f, Animation.RELATIVE_TO_SELF, 0.0f
    )
    slide.apply {
        duration = 200
        fillAfter = true
        isFillEnabled = true
    }

    view.startAnimation(slide)
}

/**
 * 设置ImageView的图片资源,使用统一的主题
 */
fun ImageView.setDrawable(@DrawableRes res: Int) {
    setImageDrawable(
        ContextCompat.getDrawable(context, res)
    )
}

fun TextView.setColorForText(@ColorRes res: Int) {
    setTextColor(ContextCompat.getColor(context, res))
}

/**
 * 设置textView的字重
 * @param weight 字重  单位 px
 */
fun TextView.setTextWeight(weight: Float) {
    this.paint.style = Paint.Style.FILL_AND_STROKE
    this.paint.strokeWidth = weight
}

fun TextView.setMiddleWeight() {
    this.paint.style = Paint.Style.FILL_AND_STROKE
    this.paint.strokeWidth = resources.displayMetrics.density * 0.5f
}

fun TextView.setBoldWeight() {
    this.paint.style = Paint.Style.FILL_AND_STROKE
    this.paint.strokeWidth = resources.displayMetrics.density * 0.8f
}

/**
 * 隐藏view
 */
fun View?.gone() {
    this?.visibility = View.GONE
}

/**
 * 显示view
 */
fun View?.visible() {
    this?.visibility = View.VISIBLE
}


/** 防抖 */
const val FAST_CLICK_TRIGGER = 300

inline fun View.onDebounceClickListener(crossinline block: () -> Unit) {
    // 如果不是快速点击，则响应点击逻辑
    setOnClickListener { if (!it.isFastClick()) block() }
}

// 判断是否快速点击
fun View.isFastClick(): Boolean {
    val currentTime = System.currentTimeMillis()
    return if (currentTime - triggerTime >= FAST_CLICK_TRIGGER) {
        triggerTime = currentTime
        false
    } else {
        true
    }
}

// 记录上次点击时间
private var View.triggerTime: Long
    get() = tag as? Long ?: 0L
    set(value) {
        tag = value
    }


/**
 * 设置View圆角
 */
class RoundedViewOutlineProvider(private val radius: Int = 0) : ViewOutlineProvider() {

    override fun getOutline(view: View?, outline: Outline?) {
        val width = view?.width ?: 0
        val height = view?.height ?: 0
        outline?.setRoundRect(0, 0, width, height, radius.toFloat())
    }
}