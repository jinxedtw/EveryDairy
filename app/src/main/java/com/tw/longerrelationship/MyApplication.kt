package com.tw.longerrelationship

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.baidu.mapapi.SDKInitializer
import androidx.datastore.core.DataStore
import com.tw.longerrelationship.util.dataStore
import com.tw.longerrelationship.util.logV
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.sevenheaven.gesturelock.GestureLock
import com.tw.longerrelationship.views.activity.GestureLockActivity


class MyApplication : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        logV("程序可用最大内存:", (Runtime.getRuntime().maxMemory() / 1024).toString() + "k")
        appContext = this

        // 初始化百度SDK
        SDKInitializer.initialize(applicationContext)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }


    /**
     * 应用进入前台
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForeground() {
        GestureLockActivity.open(appContext, GestureLock.MODE_NORMAL)
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