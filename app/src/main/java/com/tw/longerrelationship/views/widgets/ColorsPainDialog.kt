package com.tw.longerrelationship.views.widgets

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.tw.longerrelationship.views.activity.DairyEditActivity
import android.view.*
import androidx.fragment.app.DialogFragment
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.slideToUp

/**
 * [DairyEditActivity]修改背景弹框
 */
class ColorsPainDialog(val activity: DairyEditActivity) : DialogFragment() {
    var mView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.layout_colors_select, container, false)
        return mView
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        //设置dialog相应属性
        val params = window!!.attributes
        params.gravity = Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = params
        //必须设定的属性，否则无法使dialog铺满屏幕，设置其他颜色会出现黑边
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        activity.closeKeyboard()
        slideToUp(mView!!)
    }
}