package com.tw.longerrelationship

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LifecycleObserver
import com.baidu.mapapi.SDKInitializer
import com.tw.longerrelationship.util.dataStore
import com.tw.longerrelationship.util.logV


class MyApplication : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        logV("程序可用最大内存:", (Runtime.getRuntime().maxMemory() / 1024).toString() + "k")
        appContext = this

        // 初始化百度SDK
        SDKInitializer.initialize(applicationContext)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Context

        /**
         * 获取DataStore实例。
         */
        val dataStore: DataStore<Preferences> by lazy {
            appContext.dataStore
        }
    }
}