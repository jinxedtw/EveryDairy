package com.tw.longerrelationship.views.widgets

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat.startActivityForResult
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.requestSDCardWritePermission
import com.tw.longerrelationship.util.setOnClickListeners
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
        val llRecording: LinearLayout = findViewById(R.id.ll_recording)

        // 相机，图库，录音的点击入口
        setOnClickListeners(llCamera, llPicture, llRecording) {
            when (this) {
                llCamera -> {
                    openCamera()
                    dismiss()
                }
                // 后期可以换成自定义相册,实现一次性选取多张图片
                llPicture -> {
//                    requestSDCardWritePermission(activity)
                    openAlbum()

                    dismiss()
                }
                llRecording -> {
                    openRecording()
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
     * 调用系统录音
     */
    private fun openRecording() {
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        startActivityForResult(activity, intent, 3, null)
    }
}