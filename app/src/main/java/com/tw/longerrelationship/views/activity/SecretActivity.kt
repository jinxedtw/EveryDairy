package com.tw.longerrelationship.views.activity

import androidx.lifecycle.lifecycleScope
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivitySecretBinding
import com.tw.longerrelationship.util.DataStoreUtil
import com.tw.longerrelationship.util.CipherUtil
import com.tw.longerrelationship.util.setOnClickListeners
import com.tw.longerrelationship.util.showToast
import kotlinx.coroutines.launch

class SecretActivity : BaseActivity<ActivitySecretBinding>() {
    override fun init() {
        initBindingWithAppBar()
        setAppBarTitle("私密空间")
        initView()
    }

    private fun initView() {
        setOnClickListeners(
            mBinding.btSetComplete,
            mBinding.btLogin
        ) {
            when (this) {
                mBinding.btSetComplete -> {
                    handlePassword()
                }
                mBinding.btLogin -> {
                    val encodeStr = DataStoreUtil.getSyncData(DAIRY_PASSWORD, "")

                    val key=CipherUtil.getAESKey(mBinding.etLogin.text.toString())
                    if (CipherUtil.decryptAES(encodeStr, key).equals(SECRET_STRING)) {
                        showToast(this@SecretActivity, "登录成功")
                    }

                }
            }
        }
    }

    private fun handlePassword() {
        val key =  CipherUtil.getAESKey(mBinding.etInputPassword.text.toString())

        lifecycleScope.launch {
            if (mBinding.etInputPassword.text.isNotEmpty()) {
                DataStoreUtil.putData(
                    DAIRY_PASSWORD,
                    CipherUtil.encryptAES(SECRET_STRING,key)
                )
            }
        }
    }


    override fun getLayoutId(): Int = R.layout.activity_secret

    companion object {
        const val DAIRY_PASSWORD = "dairy_password"
        const val SECRET_STRING = "阿巴阿巴阿巴嘟嘟嘟啦啦啦啦"
        const val ENCODE_STRING = "encode_string"
    }
}