package com.tw.longerrelationship.views.activity

import androidx.lifecycle.lifecycleScope
import com.tw.longerrelationship.MyApplication
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivitySecretBinding
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.util.Constants.KEY_DAIRY_PASSWORD
import kotlinx.coroutines.launch

class SecretActivity : BaseActivity<ActivitySecretBinding>() {
    override fun init() {
        initBindingWithAppBar("私密空间")
        initView()
    }

    private fun initView() {
        setOnClickListeners(
            mBinding.btSetComplete,
            mBinding.btLogin,
            mBinding.btAudioRecord,
        ) {
            when (this) {
                mBinding.btSetComplete -> {
                    handlePassword()
                }
                mBinding.btLogin -> {
                    val encodeStr = DataStoreUtil[KEY_DAIRY_PASSWORD] ?: ""

                    val key = CipherUtil.getAESKey(mBinding.etLogin.text.toString())
                    if (CipherUtil.decryptAES(encodeStr, key).equals(SECRET_STRING)) {
                        showToast("登录成功")
                    }

                }
                mBinding.btAudioRecord->{
                }
            }
        }
    }

    private fun handlePassword() {
        val key = CipherUtil.getAESKey(mBinding.etInputPassword.text.toString())

        lifecycleScope.launch {
            if (mBinding.etInputPassword.text.isNotEmpty()) {
                DataStoreUtil.putData(KEY_DAIRY_PASSWORD, CipherUtil.encryptAES(SECRET_STRING, key))
            }
        }
    }


    override fun getLayoutId(): Int = R.layout.activity_secret

    companion object {
        private const val SECRET_STRING = "阿巴阿巴阿巴嘟嘟嘟啦啦啦啦"
        const val ENCODE_STRING = "encode_string"
    }
}