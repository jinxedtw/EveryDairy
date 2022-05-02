package com.tw.longerrelationship.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.view.animation.Animation
import androidx.annotation.ColorInt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tw.longerrelationship.MyApplication.Companion.appContext
import com.tw.longerrelationship.util.annotation.ViewState
import java.text.SimpleDateFormat
import java.util.*


/**
 * 批量设置控件点击事件。
 *
 * @param v 点击的控件
 * @param block 处理点击事件回调代码块
 */
fun setOnClickListeners(vararg v: View?, block: View.() -> Unit) {
    val listener = View.OnClickListener {
        it.block()
    }
    v.forEach {
        it?.setOnClickListener(listener)
    }
}

/**
 * 批量处理view的状态
 */
fun setMultiViewState(@ViewState state: Int, vararg v: View?) {
    v.forEach {
        it?.visibility = state
    }
}

/**
 * 定位权限动态申请
 */
fun requestPositioningPermission(context: Activity?) {
    // 网络定位
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    1
                )
            }
        }
    }.onFailure {}

    // GPS定位
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    2
                )
            }
        }
    }.onFailure { }
}

/**
 * SD卡读写权限动态申请
 */
fun requestSDCardWritePermission(context: Activity) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 3
        )
    }
}

/**
 * 录音权限
 */
fun requestRecordAudioPermission(context: Activity) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context, arrayOf(Manifest.permission.RECORD_AUDIO), 1
        )
    }
}

/**
 * 把两个时间作比较
 * 获得需要的时间字符串
 */
fun getComparedTime(data: Date): String {
    val old: Calendar = Calendar.getInstance().apply { time = data }
    val now: Calendar = Calendar.getInstance()

    when {
        now.get(Calendar.YEAR) - old.get(Calendar.YEAR) >= 1 -> {
            return "${old.get(Calendar.YEAR)}年"
        }
        now.get(Calendar.DAY_OF_YEAR) - old.get(Calendar.DAY_OF_YEAR) == 1 -> {
            return "昨天"
        }
        now.get(Calendar.DAY_OF_YEAR) - old.get(Calendar.DAY_OF_YEAR) > 1 -> {
            return "${old.get(Calendar.MONTH) + 1}/${old.get(Calendar.DAY_OF_MONTH)}"
        }
    }
    return getHourMinuteTime(old)
}

fun isSameDay(date1: Date?, date2: Date?): Boolean {
    require(!(date1 == null || date2 == null)) { "The date must not be null" }
    val cal1 = Calendar.getInstance()
    cal1.time = date1
    val cal2 = Calendar.getInstance()
    cal2.time = date2
    return isSameDay(cal1, cal2)
}


fun isSameDay(cal1: Calendar?, cal2: Calendar?): Boolean {
    require(!(cal1 == null || cal2 == null)) { "The date must not be null" }
    return cal1[Calendar.ERA] == cal2[Calendar.ERA] && cal1[Calendar.YEAR] == cal2[Calendar.YEAR] && cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
}


/** 获得右边格式的时间  eg: 18:00,07:08*/
fun getHourMinuteTime(calendar: Calendar) =
    "${
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) "0${calendar.get(Calendar.HOUR_OF_DAY)}"
        else calendar.get(Calendar.HOUR_OF_DAY)
    }:${
        if (calendar.get(Calendar.MINUTE) < 10) "0${calendar.get(Calendar.MINUTE)}"
        else calendar.get(Calendar.MINUTE)
    }"

/**
 * 获得右边的时间格式 eg: 2021/3/15
 */
fun getYearAndDay(calendar: Calendar) =
    "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}"


inline fun Animation.addAnimListener(
    crossinline onAnimationStart: (animation: Animation?) -> Unit = { _ -> },
    crossinline onAnimationEnd: (animation: Animation?) -> Unit = { _ -> },
    crossinline onAnimationRepeat: (animation: Animation?) -> Unit = { _ -> },
): Animation.AnimationListener {
    val animWatcher = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            onAnimationStart.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animation?) {
            onAnimationEnd.invoke(animation)
        }

        override fun onAnimationRepeat(animation: Animation?) {
            onAnimationRepeat.invoke(animation)
        }
    }

    setAnimationListener(animWatcher)
    return animWatcher
}

fun dp2px(dp: Float): Float {
    return (dp * (appContext.resources.displayMetrics.densityDpi) / 160)
}

fun dp2px(dp: Int): Int {
    return (dp * (appContext.resources.displayMetrics.densityDpi) / 160)
}

fun getScreenWidth(): Int {
    return appContext.resources.displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return appContext.resources.displayMetrics.heightPixels
}

/**
 * 16进制转GRB颜色值方法
 * @param hex eg: #fe92ec
 */
fun toRGB(hex: String) {
    val color = hex.replace("#", "").toInt(16)
    val red = color and 0xff0000 shr 16
    val green = color and 0x00ff00 shr 8
    val blue = color and 0x0000ff
    println("red=$red--green=$green--blue=$blue")
}

/**
 * GRB转16进制颜色值方法
 */
fun toHex(red: Int, green: Int, blue: Int) {
    val hr = Integer.toHexString(red)
    val hg = Integer.toHexString(green)
    val hb = Integer.toHexString(blue)
    println("#$hr$hg$hb")
}

/**
 * 给color添加透明度
 * @param alpha 透明度 0f～1f
 * @param baseColor 基本颜色
 */
fun getColorWithAlpha(alpha: Float, @ColorInt baseColor: Int): Int {
    val a = 255.coerceAtMost(0.coerceAtLeast((alpha * 255).toInt())) shl 24
    val rgb = 0x00ffffff and baseColor
    return a + rgb
}

/** 打印代码块的执行时间 */
inline fun <T> T.runTimeLog(message: String? = null, block: T.() -> Unit) {
    val startTime = System.currentTimeMillis()
    block()
    logD("$message@runTimeLog", "${System.currentTimeMillis() - startTime}ms")
}

inline fun runTimeLog(message: String? = null, block: () -> Unit) {
    val startTime = System.currentTimeMillis()
    block()
    logD("$message@runTimeLog", "${System.currentTimeMillis() - startTime}ms")
}

inline fun runTimePrint(block: () -> Unit) {
    val startTime = System.currentTimeMillis()
    block()
    println("花费时间:${System.currentTimeMillis() - startTime}ms")
}

inline fun <R> runReportCatching(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(e)
        e.printStackTrace()
        Result.failure(e)
    }
}

