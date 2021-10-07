package com.tw.longerrelationship.views.widgets

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import com.tw.longerrelationship.R

class YoloTextView(context: Context, attributeSet: AttributeSet?) :
    androidx.appcompat.widget.AppCompatTextView(context, attributeSet) {

    private var textStyle = -1

    init {
        initView(context, attributeSet)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.YoloTextView, 0, 0)
        textStyle = typedArray.getInt(R.styleable.YoloTextView_YoloTextStyle, -1)
        typedArray.recycle()

        when (textStyle) {
            0->{
                // 普通体
            }
            1 -> {
                // 中黑体
                setMiddleWeight()
            }
            2->{
                // 粗体
                setBoldWeight()
            }
        }
    }

    private fun setMiddleWeight() {
        this.paint.style = Paint.Style.FILL_AND_STROKE
        this.paint.strokeWidth = resources.displayMetrics.density * 0.5f
    }

    private fun setBoldWeight() {
        this.paint.style = Paint.Style.FILL_AND_STROKE
        this.paint.strokeWidth = resources.displayMetrics.density * 0.8f
    }
}