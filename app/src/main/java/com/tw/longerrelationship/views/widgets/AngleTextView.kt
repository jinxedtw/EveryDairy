package com.tw.longerrelationship.views.widgets

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.dp2px

/**
 * 带弧度的textView
 */
class AngleTextView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var textColor = 0              // 文字颜色
    private var text: String? = null       // 文本
    private var angles = 0f                // 文字弧度
    private var paint: Paint = Paint()     // 画笔
    private var path: Path = Path()        // 路径
    private var mTextSize = 0f             // 文本大小
    private var mWidth = 0                 // 宽
    private var mHeight = 0                // 高
    private var mOffsetY = 0f                 // 竖直偏移

    private fun initView(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.AngleTextView, 0, 0)
        textColor = typedArray.getResourceId(R.styleable.AngleTextView_angle_text_color, Color.BLACK)
        text = typedArray.getString(R.styleable.AngleTextView_angle_text_content)
        mTextSize = typedArray.getFloat(R.styleable.AngleTextView_angle_text_size, 22f)
        angles = typedArray.getFloat(R.styleable.AngleTextView_angle_text_angles, 180f)
        mOffsetY = typedArray.getFloat(R.styleable.AngleTextView_angle_text_offsetY, 0f)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMod = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMod = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        when (widthMod) {
            MeasureSpec.AT_MOST -> mWidth = DEFAULT_WIDTH
            MeasureSpec.EXACTLY -> mWidth = widthSize
            MeasureSpec.UNSPECIFIED -> mWidth = widthSize
        }
        when (heightMod) {
            MeasureSpec.AT_MOST -> mHeight = DEFAULT_HEIGHT
            MeasureSpec.EXACTLY -> mHeight = heightSize
            MeasureSpec.UNSPECIFIED -> mHeight = heightSize
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rectF = RectF(0f, 0f, mWidth.toFloat(), mHeight.toFloat())
        paint.apply {
            isAntiAlias = true
            color = textColor
            typeface = Typeface.DEFAULT_BOLD
            textSize = dp2px(context, mTextSize)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                letterSpacing = 0.1.toFloat()
            }
        }
        val vOffset:Float = if (mOffsetY != 0f) mOffsetY else mHeight.toFloat()

        // 设置圆弧路径是对称的
        path.addArc(rectF, angles, 360 - (angles - 180) - angles)
        canvas.drawTextOnPath(text!!, path, 0f, vOffset, paint)
    }

    companion object {
        //默认宽度
        private const val DEFAULT_WIDTH = 600

        //默认高度
        private const val DEFAULT_HEIGHT = 150
    }

    init {
        initView(context, attrs)
    }
}