package com.tw.longerrelationship.views.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.setDrawable

class CommonBar(context: Context, attributes: AttributeSet) :
    ConstraintLayout(context, attributes) {
    private lateinit var mTitle: TextView
    private lateinit var rightTextView: TextView
    private lateinit var rightImageView: ImageView

    init {
        View.inflate(context, R.layout.layout_common_bar, this)
        initView()
        setTypedArrayValue(context, attributes)
    }

    private fun initView() {
        mTitle = findViewById(R.id.tv_title)
        rightImageView = findViewById(R.id.iv_right)
        rightTextView = findViewById(R.id.tv_right)
    }

    private fun setTypedArrayValue(context: Context, attributes: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attributes, R.styleable.CommonBar)
        val title = typedArray.getString(R.styleable.CommonBar_title)
        val rightText = typedArray.getString(R.styleable.CommonBar_textOfRight)
        val rightImage = typedArray.getResourceId(R.styleable.CommonBar_imageOfRight, 0)
        typedArray.recycle()
        mTitle.text = title
        rightImageView.apply {
            if (rightImage != 0) {
                setDrawable(rightImage)
                visibility = View.VISIBLE
            }
        }
        rightTextView.apply {
            text = rightText
            visibility = View.VISIBLE
        }
    }


    fun setRightClickAction(action: () -> Unit) {
        rightTextView.setOnClickListener { action() }
        rightTextView.setOnClickListener { action() }
    }
}