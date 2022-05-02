package com.tw.longerrelationship.views.activity

import android.content.Intent
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.tw.longerrelationship.MyApplication
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.network.ServiceCreator
import com.tw.longerrelationship.util.DataStoreUtil
import com.tw.longerrelationship.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 开屏页面，判断是否是第一次进入App
 */
class SplashActivity : BaseActivity<ViewDataBinding>() {
    override fun init() {
        setContentView(getLayoutId())

//        MyApplication.mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
//            if (task.isSuccessful) {
//                val updated = task.result
//                Log.d(tag, "Config params updated: $updated")
//                showToast("Fetch and activate succeeded")
//            } else {
//                showToast( "Fetch failed")
//            }
//        }

        startActivity(
            Intent(
                this@SplashActivity,
                if (isFirstOpenApp) {
                    isFirstOpenApp = false
                    GuideActivity::class.java
                } else {
                    HomeActivity::class.java
                }
            )
        )
        finish()
    }

    override fun getLayoutId(): Int = R.layout.activity_splash

    companion object {
        private const val FIRST_OPEN_APP = "isFirstOpenApp"

        var isFirstOpenApp: Boolean
            get() = DataStoreUtil[FIRST_OPEN_APP] ?: true
            set(value) {
                CoroutineScope(Dispatchers.IO).launch {
                    DataStoreUtil.saveBooleanData(FIRST_OPEN_APP, value)
                }
            }
    }
}