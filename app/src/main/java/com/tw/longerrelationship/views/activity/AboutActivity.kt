package com.tw.longerrelationship.views.activity

import androidx.core.graphics.drawable.toBitmap
import com.tw.longerrelationship.BuildConfig
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivityAboutBinding
import com.tw.longerrelationship.util.savePicToAlbum
import com.tw.longerrelationship.util.setOnClickListeners
import com.tw.longerrelationship.util.shareImage

class AboutActivity : BaseActivity<ActivityAboutBinding>() {
    override fun init() {
        initBindingWithAppBar("关于")
        initView()
    }

    private fun initView() {
        if (BuildConfig.DEBUG) {
            mBinding.tvSettingAppVersion.text = "V" + BuildConfig.VERSION_NAME + " debug"
        } else {
            mBinding.tvSettingAppVersion.text = "V" + BuildConfig.VERSION_NAME
        }


        setOnClickListeners(mBinding.btSaveQrCode, mBinding.btShare) {
            when (this) {
                mBinding.btSaveQrCode -> savePicToAlbum(mBinding.ivQrCode.drawable.toBitmap())
                mBinding.btShare -> {
                    shareImage(mBinding.ivQrCode.drawable.toBitmap())
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_about
}