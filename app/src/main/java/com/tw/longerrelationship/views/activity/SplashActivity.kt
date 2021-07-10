package com.tw.longerrelationship.views.activity

import android.content.Intent
import android.view.View
import com.tw.longerrelationship.R

/**
 * 开屏页面，判断是否是第一次进入App
 */
class SplashActivity : BaseActivity() {
    override fun init(): View? {
        setContentView(getLayoutId())

        val isFirstOpenApp = sharedPreferences.getBoolean(FIRST_OPEN_APP, true)
        if (isFirstOpenApp) {
            sharedPreferences.edit().putBoolean(FIRST_OPEN_APP, false).apply()
        }

        startActivity(
            Intent(
                this@SplashActivity,
                if (isFirstOpenApp) GuideActivity::class.java else MainActivity::class.java
            )
        )
        finish()
        return null
    }

    override fun getLayoutId(): Int = R.layout.activity_splash

    companion object {
        const val FIRST_OPEN_APP = "isFirstOpenApp"
    }
}