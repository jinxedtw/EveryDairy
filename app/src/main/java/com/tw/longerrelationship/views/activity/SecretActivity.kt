package com.tw.longerrelationship.views.activity

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
        mBinding.sbLock.isEnabled = false

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

    private fun initEvent() {
        LiveDataBus.with(Constants.LIVE_SET_LOCK, Boolean::class.java).observe(this) {
            if (it){
                mBinding.sbLock.isEnabled = true
                mBinding.sbLock.isChecked = true
                DataStoreUtil[KEY_GESTURE_LOCK] = true
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_secret
}