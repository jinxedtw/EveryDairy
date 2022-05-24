package com.tw.longerrelationship.views.widgets

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.requestSDCardWritePermission
import com.tw.longerrelationship.util.setOnClickListeners
import com.tw.longerrelationship.util.showToast
import com.tw.longerrelationship.views.activity.DairyEditActivity


class PictureTypeSelectDialog : Dialog {
    var activity: Activity

    constructor(context: Context) : super(context) {

        activity = context as Activity
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        activity = context as Activity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(View.inflate(context, R.layout.layout_picture_type_dialog, null))

        initView()
    }

    private fun initView() {
        val llCamera: LinearLayout = findViewById(R.id.ll_camera)
        val llPicture: LinearLayout = findViewById(R.id.ll_picture)
        val llPaint: LinearLayout = findViewById(R.id.ll_paint)

        // 相机，图库，录音的点击入口
        setOnClickListeners(llCamera, llPicture, llPaint) {
            when (this) {
                llCamera -> {
                    openCamera()
                    dismiss()
                }
                llPicture -> {
                    requestSDCardWritePermission(activity)
                    openAlbum()
                    dismiss()
                }
                llPaint -> {
                    openDoodle()
                    dismiss()
                }
            }
        }
    }

    /**
     * 打开相册
     */
    private fun openAlbum() {
        (activity as DairyEditActivity).openAlbum()
    }

    /**
     * 打开相机
     */
    private fun openCamera() {
        (activity as DairyEditActivity).openCamera()
    }

    /**
     * 打开画板
     */
    private fun openDoodle(){
        (activity as DairyEditActivity).openDoodle()
    }

}