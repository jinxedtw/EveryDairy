package com.tw.longerrelationship.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.view.animation.Animation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
 * 获取手机厂商
 *
 * @return  手机厂商
 * eg: Realme
 */
fun getDeviceBrand(): String {
    return Build.BRAND
}

/**
 * 获取当前手机系统版本号
 *
 * @return  系统版本号
 */
fun getSystemVersion(): String {
    return Build.VERSION.RELEASE
}

/**
 * 定位权限动态申请
 */
fun requestPositioningPermission(context: Activity?) {
    // 网络定位
    try {
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
    } catch (e: SecurityException) {
    }

    // GPS定位
    try {
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
    } catch (e: SecurityException) {
    }
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
 *  获得当前时间
 *  格式 20:39
 */
@SuppressLint("SimpleDateFormat")
fun getNowTimeHour(date: Date): String {
    return SimpleDateFormat("HH:mm").format(date)
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

/**
 * 获得右边格式的时间  eg: 18:00,07:08
 */
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

