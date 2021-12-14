package com.tw.longerrelationship.views.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import com.bumptech.glide.Glide
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivityAlbumImageInfoBinding
import com.tw.longerrelationship.util.Constants.INTENT_ALBUM_CHECKBOX_STATE
import com.tw.longerrelationship.util.Constants.INTENT_IMAGE_URL
import com.tw.longerrelationship.util.setAndroidNativeLightStatusBar
import com.tw.longerrelationship.util.setOnClickListeners
import com.tw.longerrelationship.util.setStatusBarColor

class AlbumImageInfoActivity : BaseActivity<ActivityAlbumImageInfoBinding>() {
    private val imageUrl by lazy { intent.getStringExtra(INTENT_IMAGE_URL) }
    private val checkBoxState by lazy { intent.getBooleanExtra(INTENT_ALBUM_CHECKBOX_STATE, false) }

    override fun init() {
        setAndroidNativeLightStatusBar(this, false)
        initBinding()
        setStatusBarColor(Color.TRANSPARENT)
        initView()
    }

    private fun initView() {
        mBinding.checkBox.isChecked = checkBoxState
//        Glide.with(this).asBitmap().load(imageUrl)
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    mBinding.albumPhotoView.initBitMap(bitmap = resource)
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {
//                }
//
//            })
        Glide.with(this).load(imageUrl).into(mBinding.albumPhotoView)

        setOnClickListeners(mBinding.ivGoBack) {
            when (this) {
                mBinding.ivGoBack -> {
                    val intent = Intent().apply {
                        putExtra(INTENT_ALBUM_CHECKBOX_STATE, mBinding.checkBox.isChecked)
                    }
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent().apply {
            putExtra(INTENT_ALBUM_CHECKBOX_STATE, mBinding.checkBox.isChecked)
        }
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    override fun getLayoutId(): Int = R.layout.activity_album_image_info
}