package com.tw.longerrelationship

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.baidu.mapapi.SDKInitializer
import androidx.datastore.core.DataStore
import com.tw.longerrelationship.util.dataStore
import com.tw.longerrelationship.util.logV
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        logV("程序可用最大内存:", (Runtime.getRuntime().maxMemory() / 1024).toString() + "k")
        appContext=this

        // 初始化百度SDK
        SDKInitializer.initialize(applicationContext)


        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Context
        val mFirebaseRemoteConfig by lazy {
            FirebaseRemoteConfig.getInstance()
        }

        /**
         * 获取DataStore实例。
         */
        val dataStore: DataStore<Preferences> by lazy {
            appContext.dataStore
        }
    }
}