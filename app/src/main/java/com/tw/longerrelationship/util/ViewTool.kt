package com.tw.longerrelationship.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


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
 * 封装Toast
 */
fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, duration).show()
}

/**
 * 透明状态栏
 *
 * 调用此方法后状态栏为透明，整体布局会向上扩展
 * 需要动态预留出状态栏的高度
 */
fun makeStatusBarTransparent(activity: Activity) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        return
    }
    val window = activity.window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val option =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}

/**
 * 谷歌原生方式改变状态栏文字颜色
 */
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

/**
 * 创建用来存储图片的文件，以时间来命名就不会产生命名冲突
 *
 * @return 创建的图片文件的Uri(APP的外部私有目录)
 */
@SuppressLint("SimpleDateFormat")
fun createImageFile(context: Context): Uri {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"

    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DCIM)
    var imageFile: File? = null

    try {
        imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return imageFile!!.toUri()
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

/**
 * 弹出动画
 * @param view
 */
fun slideToUp(view: View) {
    val slide: Animation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 0.0f,
        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
        1.0f, Animation.RELATIVE_TO_SELF, 0.0f
    )
    slide.apply {
        duration = 300
        fillAfter = true
        isFillEnabled = true
    }

    view.startAnimation(slide)
    slide.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {}
        override fun onAnimationRepeat(animation: Animation?) {}
    })
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
 * @param weight 字重
 */
fun TextView.setTextWeight(weight: Float) {
    this.paint.style = Paint.Style.FILL_AND_STROKE;
    this.paint.strokeWidth = weight
}
