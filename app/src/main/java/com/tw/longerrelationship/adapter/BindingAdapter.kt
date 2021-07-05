package com.tw.longerrelationship.adapter

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.tw.longerrelationship.R


@BindingAdapter("isChanged")
fun bindIsChanged(view: View, isChanged: Boolean) {
    view.setBackgroundResource(if (isChanged) R.drawable.ic_select else R.drawable.ic_close)
}

@BindingAdapter("isFold")
fun bindIsFold(view: ImageView, isFold: Boolean) {
    view.setImageDrawable(
        ContextCompat.getDrawable(
            view.context,
            if (isFold) R.drawable.pic_unfold else R.drawable.pic_fold
        )
    )
}


