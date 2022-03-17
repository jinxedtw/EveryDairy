package com.tw.longerrelationship.views.widgets

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tw.longerrelationship.R
import com.tw.longerrelationship.adapter.DialogIconsAdapter
import com.tw.longerrelationship.help.GridItemDecoration
import com.tw.longerrelationship.views.activity.DairyEditActivity

class IconSelectDialog(
    context: Context,
    themResId: Int,
    val type: Int,
    val iconClick: (Drawable, Int) -> Unit
) :
    Dialog(context, themResId) {

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = View.inflate(context, R.layout.layout_icon_select_dialog, null)
        setContentView(view)
        cancerOnFocus()
        initView(type)
    }

    /**
     * 点击不会抢占焦点,但是会导致点击dialog外不消失
     * 解决方法:监听dialog以外的事件来cancer掉dialog
     * [DairyEditActivity.dispatchTouchEvent]
     * [DairyEditActivity.dispatchKeyEvent]
     */
    private fun cancerOnFocus() {
        setCanceledOnTouchOutside(true)
        window!!.apply {
            setGravity(Gravity.CENTER)
            setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
    }

    /**
     * 根据type类型来创建天气选择和心情选择两个dialog
     * type==1 天气选择
     * type==2 心情选择
     */
    private fun initView(type: Int) {
        val iconsList =
            if (type == 1)
                arrayListOf(
                    R.drawable.ic_sunny,
                    R.drawable.ic_night,
                    R.drawable.ic_cloudy,
                    R.drawable.ic_rain,
                    R.drawable.ic_big_rain,
                    R.drawable.ic_cloudy_night,
                    R.drawable.ic_fog,
                    R.drawable.ic_snow,
                    R.drawable.ic_haze
                )
            else
                arrayListOf(
                    R.drawable.ic_mood,
                    R.drawable.ic_mood,
                    R.drawable.ic_mood,
                    R.drawable.ic_nomal,
                    R.drawable.ic_nomal,
                    R.drawable.ic_nomal,
                    R.drawable.ic_sad,
                    R.drawable.ic_sad,
                    R.drawable.ic_sad
                )
        findViewById<TextView>(R.id.tv_type).text = if (type == 1) "今日天气" else "当下心情"

        recyclerView = findViewById(R.id.rc_icon_list)
        recyclerView.addItemDecoration(GridItemDecoration(3,25))
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = DialogIconsAdapter(iconsList) { drawable, iconId ->
            iconClick(drawable, iconId)
            dismiss()
        }
    }
}