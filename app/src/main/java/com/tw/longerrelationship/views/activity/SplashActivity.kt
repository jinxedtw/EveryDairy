package com.tw.longerrelationship.views.activity

import android.content.Intent
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.sevenheaven.gesturelock.GestureLock
import com.tw.longerrelationship.MyApplication
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.network.ServiceCreator
import com.tw.longerrelationship.util.Constants
import com.tw.longerrelationship.util.DataStoreUtil
import com.tw.longerrelationship.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ViewDataBinding>() {
    override fun init() {
        setContentView(getLayoutId())

        if (DataStoreUtil[Constants.KEY_GESTURE_LOCK] ?: false) {
            GestureLockActivity.open(this, GestureLock.MODE_NORMAL)
        }else{
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
        }
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