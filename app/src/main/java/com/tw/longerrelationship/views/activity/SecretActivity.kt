package com.tw.longerrelationship.views.activity

import android.annotation.SuppressLint
import com.sevenheaven.gesturelock.GestureLock
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivitySecretBinding
import com.tw.longerrelationship.logic.LiveDataBus
import com.tw.longerrelationship.util.Constants
import com.tw.longerrelationship.util.Constants.KEY_GESTURE_LOCK
import com.tw.longerrelationship.util.DataStoreUtil
import com.tw.longerrelationship.util.setOnClickListeners

class SecretActivity : BaseActivity<ActivitySecretBinding>(){
    override fun init() {
        initBindingWithAppBar("设置密码锁")
        initView()
        initEvent()
    }

    private fun initView() {
        mBinding.sbLock.isChecked = DataStoreUtil[KEY_GESTURE_LOCK] ?: false

        setOnClickListeners(mBinding.clLock) {
            when (this) {
                mBinding.clLock -> {
                    if (mBinding.sbLock.isChecked) {
                        mBinding.sbLock.isChecked = false
                        DataStoreUtil[KEY_GESTURE_LOCK] = false
                    } else {
                        GestureLockActivity.open(this@SecretActivity, GestureLock.MODE_EDIT)
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEvent() {
        LiveDataBus.with<Boolean>(Constants.LIVE_SET_LOCK).observe(this) {
            if (it){
                mBinding.sbLock.isChecked = true
                DataStoreUtil[KEY_GESTURE_LOCK] = true
            }
        }

        mBinding.sbLock.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_secret
}