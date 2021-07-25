package com.tw.longerrelationship.views.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.logV
import com.tw.longerrelationship.util.setDrawable
import com.tw.longerrelationship.util.setOnClickListeners
import com.tw.longerrelationship.util.showToast
import com.tw.longerrelationship.views.activity.DairyEditActivity


class DairyEditBar(context: Context, attributes: AttributeSet) : LinearLayout(context, attributes) {

    lateinit var mTitle: TextView
    lateinit var leftIcon: ImageView
    lateinit var rightIcon: ImageView

    private var mySelf: DairyEditBar =
        View.inflate(context, R.layout.layout_dairy_edit_bar, this) as DairyEditBar


    init {
        initView()
        setTypedArrayValue(context, attributes)
    }

    private fun setTypedArrayValue(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DairyEditBar)
        val title = typedArray.getString(R.styleable.DairyEditBar_centerTitle)
        val leftImage =
            typedArray.getResourceId(R.styleable.DairyEditBar_leftImage, R.drawable.ic_close)
        val rightImage =
            typedArray.getResourceId(R.styleable.DairyEditBar_rightImage, R.drawable.ic_more_list)
        typedArray.recycle()

        mTitle.text = title
        leftIcon.setDrawable(leftImage)
        rightIcon.setDrawable(rightImage)
    }

    private fun initView() {
        mTitle = findViewById(R.id.tv_title)
        leftIcon = findViewById(R.id.left_icon)
        rightIcon = findViewById(R.id.right_icon)

        setOnClickListeners(leftIcon, rightIcon) {
            when (this) {
                leftIcon -> {
                    if (mySelf.tag == "changed") {
                        // 保存日记
                        (context as DairyEditActivity).saveDairy()
                    } else {
                        // 退出当前Activity
                        (context as DairyEditActivity).finishActivity()
                    }
                }
                rightIcon -> {
                    showPopupMenu()
                }
            }
        }
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(context, rightIcon)
        popupMenu.inflate(R.menu.menu_dairy_edit)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.it_set_private -> {
                    showToast(context, "设为私密")
                }
                R.id.it_set_love -> {
                    showToast(context, "设为喜爱")
                }
            }
            true
        }
        popupMenu.show()
    }

    fun getTitle(): String = mTitle.text.toString()
    fun setTitle(title: String) { mTitle.text = title }
}