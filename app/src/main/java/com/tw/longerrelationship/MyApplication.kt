package com.tw.longerrelationship

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.baidu.mapapi.SDKInitializer
import com.tw.longerrelationship.help.LocationService
import com.tw.longerrelationship.util.logV


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        logV("程序可用最大内存:", (Runtime.getRuntime().maxMemory() / 1024).toString() + "k")
        context = this

        // 初始化百度SDK
        SDKInitializer.initialize(context)
        locationService = LocationService(context)

        // 这里是为了解决传输file://格式的URI报错问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var locationService: LocationService
    }
}