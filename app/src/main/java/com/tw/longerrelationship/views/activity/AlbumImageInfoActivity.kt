package com.tw.longerrelationship.views.activity

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivityAlbumImageInfoBinding
import com.tw.longerrelationship.util.Constants.INTENT_IMAGE_URL
import com.tw.longerrelationship.util.setAndroidNativeLightStatusBar
import com.tw.longerrelationship.util.setOnClickListeners
import com.tw.longerrelationship.util.setStatusBarColor

class AlbumImageInfoActivity : BaseActivity<ActivityAlbumImageInfoBinding>() {
    private val imageUrl by lazy { intent.getStringExtra(INTENT_IMAGE_URL) }

    override fun init() {
        setAndroidNativeLightStatusBar(this, false)
        initBinding()
        setStatusBarColor(Color.TRANSPARENT)
        initView()
    }

    private fun initView() {
        Glide.with(this).asBitmap().load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    mBinding.albumPhotoView.initBitMap(bitmap = resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })

        setOnClickListeners(mBinding.ivGoBack) {
            when (this) {
                mBinding.ivGoBack -> {
                    finish()
                }
            }
        }
    }


    override fun getLayoutId(): Int = R.layout.activity_album_image_info
}