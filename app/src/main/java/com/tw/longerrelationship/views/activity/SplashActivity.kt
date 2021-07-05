package com.tw.longerrelationship.views.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.Window
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.Constants
import com.tw.longerrelationship.util.requestSDCardWritePermission
import com.tw.longerrelationship.util.setAndroidNativeLightStatusBar
import kotlinx.coroutines.runBlocking

/**
 * 开屏页面，判断是否是第一次进入App
 */
class SplashActivity : BaseActivity() {
    override fun init() {
        setContentView(getLayoutId())

        val sharedPreferences = baseContext.getSharedPreferences(
            Constants.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

        val isFirstOpenApp = sharedPreferences.getBoolean(FIRST_OPEN_APP, true)
        if (isFirstOpenApp) {
            sharedPreferences.edit().apply {
                putBoolean(FIRST_OPEN_APP, false)
                apply()
            }
        }

        startActivity(
            Intent(
                this@SplashActivity,
                if (isFirstOpenApp) GuideActivity::class.java else MainActivity::class.java
            )
        )

        finish()
    }

    override fun getLayoutId(): Int = R.layout.activity_splash

    companion object {
        const val FIRST_OPEN_APP = "isFirstOpenApp"
    }
}