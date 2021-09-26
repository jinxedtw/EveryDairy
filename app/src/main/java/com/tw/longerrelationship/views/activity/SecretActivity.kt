package com.tw.longerrelationship.views.activity

import androidx.lifecycle.lifecycleScope
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivitySecretBinding
import com.tw.longerrelationship.util.DataStoreUtil
import com.tw.longerrelationship.util.setOnClickListeners
import kotlinx.coroutines.launch

class SecretActivity : BaseActivity<ActivitySecretBinding>() {
    override fun init() {
        initBindingWithAppBar()
        setAppBarTitle("私密空间")
        initView()
    }

    private fun initView() {
        setOnClickListeners(mBinding.btInputComplete, mBinding.btSetComplete) {
            when (this) {
                mBinding.btInputComplete -> {

                }
                mBinding.btSetComplete -> {
                    handlePassword()
                }
            }
        }
    }

    private fun handlePassword() {
        lifecycleScope.launch {
            if (mBinding.etInputPassword.text.isNotEmpty()){
                DataStoreUtil.putData(DAIRY_PASSWORD,mBinding.etInputPassword.text)

            }
        }
    }


    override fun getLayoutId(): Int = R.layout.activity_secret

    companion object {
        const val DAIRY_PASSWORD = "dairy_password"
        const val SECRET_STRING = "阿巴阿巴阿巴嘟嘟嘟"
        const val ENCODE_STRING="encode_string"
    }
}