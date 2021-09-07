package com.tw.longerrelationship

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.baidu.mapapi.SDKInitializer
import androidx.datastore.core.DataStore
import com.tw.longerrelationship.util.dataStore
import com.tw.longerrelationship.util.logV
import androidx.datastore.preferences.core.Preferences


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        logV("程序可用最大内存:", (Runtime.getRuntime().maxMemory() / 1024).toString() + "k")
        context = this

        // 初始化百度SDK
        SDKInitializer.initialize(context)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        /**
         * 获取DataStore实例。
         */
        val dataStore: DataStore<Preferences> by lazy {
            context.dataStore
        }
    }
}