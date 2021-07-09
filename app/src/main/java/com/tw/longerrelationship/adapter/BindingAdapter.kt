package com.tw.longerrelationship.adapter

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.setDrawable


@BindingAdapter("isChanged")
fun bindIsChanged(view: View, isChanged: Boolean) {
    view.setBackgroundResource(if (isChanged) R.drawable.ic_select else R.drawable.ic_close)
}

@BindingAdapter("isFold")
fun bindIsFold(view: ImageView, isFold: Boolean) {
    view.setDrawable(if (isFold) R.drawable.pic_unfold else R.drawable.pic_fold)
}

/**
 * 给ImageView设置图片
 */
@BindingAdapter("setDrawable")
fun bindDrawable(view: ImageView, drawableId: Int) {
    view.setDrawable(drawableId)
}




