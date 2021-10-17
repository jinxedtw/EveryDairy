package com.tw.longerrelationship.views.widgets

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.tw.longerrelationship.MyApplication.Companion.context
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.dp2px
import com.tw.longerrelationship.util.setDrawable


object ToastWithImage {
    fun showToast(msg: String, isSuccess: Boolean, isShowLong: Boolean = false) {
        val toast = Toast(context)
        val toastView: View = LayoutInflater.from(context).inflate(R.layout.layout_toast, null)
        toastView.findViewById<ImageView>(R.id.iv_toast)
            .setDrawable(if (isSuccess) R.mipmap.ic_toast_success else R.mipmap.ic_toast_fail)
        toastView.findViewById<TextView>(R.id.tv_toast_content).text = msg
        toast.setGravity(Gravity.BOTTOM, 0, dp2px(56))
        toast.view = toastView
        toast.duration = if (isShowLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        toast.show()
    }
}