package com.tw.longerrelationship.views.widgets

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.tw.longerrelationship.MyApplication
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.setDrawable


object ToastWithImage {
    fun showToast(msg: String, isSuccess: Boolean) {
        val toast = Toast(MyApplication.context)
        val toastView: View = LayoutInflater.from(MyApplication.context).inflate(R.layout.layout_toast, null)
        toastView.findViewById<ImageView>(R.id.iv_img)
            .setDrawable(if (isSuccess) R.drawable.ic_mood else R.drawable.ic_sad)
        toastView.findViewById<ImageView>(R.id.iv_img).setColorFilter(Color.WHITE)
        toastView.findViewById<TextView>(R.id.tv_msg).text = msg
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.view = toastView
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}