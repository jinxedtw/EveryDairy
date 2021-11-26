package com.tw.longerrelationship.views.widgets

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.OverScroller
import androidx.annotation.Keep
import kotlin.math.max
import kotlin.math.min

class PhotoView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var bitmap: Bitmap= Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888)
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** 当前放大倍数 */
    @Keep
    var currentScale = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var smallScale = 0f             // 小放大
    private var bigScale = 0f               // 大放大
    private var isEnlarge: Boolean = false  // 是否已经放大

    private var originalOffsetX = 0f        // 初始偏移
    private var originalOffsetY = 0f
    private var offsetX = 0f                // 水平偏移距离
    private var offsetY = 0f                // 竖直偏移距离


    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(context, PhotoGestureDetector())
    }

    private val overScroller: OverScroller by lazy {
        OverScroller(context)
    }

    private val scaleGestureDetector: ScaleGestureDetector by lazy {
        ScaleGestureDetector(context, PhotoScaleGestureListener())
    }

    fun initBitMap(bitmap: Bitmap) {
        this.bitmap = bitmap
        initBitmapInfo()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 缩放系数
        val scaleFaction: Float = (currentScale - smallScale) / (bigScale - smallScale)
        // 平移
        canvas.translate(offsetX * scaleFaction, offsetY * scaleFaction)
        // 缩放
        canvas.scale(currentScale, currentScale, width / 2f, height / 2f)
        canvas.drawBitmap(bitmap, originalOffsetX, originalOffsetY, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initBitmapInfo()
    }

    /** 初始化Bitmap的一些信息 缩放信息，偏移 */
    private fun initBitmapInfo() {
        originalOffsetX = (width - bitmap.width) / 2f
        originalOffsetY = (height - bitmap.height) / 2f

        if (bitmap.width.toFloat() / bitmap.height > width.toFloat() / height) {
            // 比较宽高比,判断目标bitmap是横向还是纵向
            smallScale = width.toFloat() / bitmap.width
            bigScale = height.toFloat() / bitmap.height
        } else {
            smallScale = height.toFloat() / bitmap.height
            bigScale = width.toFloat() / bitmap.width
        }
        currentScale = smallScale
    }

    /** 接管Touch事件*/
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!overScroller.isFinished) {
            overScroller.forceFinished(true)
        }

        // 双指操作优先
        scaleGestureDetector.onTouchEvent(event)
        if (!scaleGestureDetector.isInProgress) {
            gestureDetector.onTouchEvent(event)
        }
        // 这里需要返回true，消费该次事件，不然只能接收到ACTION_DOWN事件    后面去分析详细原因
        return true
    }

    /** 处理边界 */
    private fun fixOffsets() {
        offsetX = min(offsetX, (bitmap.width * bigScale - width) / 2)               // 相当于取绝对值,获得偏移的区间值
        offsetX = max(offsetX, -(bitmap.width * bigScale - width) / 2)
        offsetY = min(offsetY, (bitmap.height * bigScale - height) / 2)
        offsetY = max(offsetY, -(bitmap.height * bigScale - height) / 2)
    }

    private fun startScaleAnimator(fromScale: Float, toScale: Float) =
        ObjectAnimator.ofFloat(this, "currentScale", fromScale, toScale).start()


    inner class PhotoGestureDetector : GestureDetector.SimpleOnGestureListener() {
        /** 双击 */
        override fun onDoubleTap(e: MotionEvent): Boolean {
            isEnlarge = !isEnlarge
            if (isEnlarge) {
                offsetX = e.x - width / 2f - (e.x - width / 2f) * bigScale / smallScale
                offsetY = e.y - height / 2f - (e.y - height / 2f) * bigScale / smallScale
                fixOffsets()
                startScaleAnimator(smallScale, bigScale)
            } else {
                startScaleAnimator(currentScale, smallScale)
            }
            return super.onDoubleTap(e)
        }

        /** 滑动 */
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (isEnlarge) {
                offsetX -= distanceX
                offsetY -= distanceY
                fixOffsets()
                invalidate()
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        /** 抛掷 */
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            if (isEnlarge) {
                overScroller.fling(
                    offsetX.toInt(), offsetY.toInt(), velocityX.toInt(), velocityY.toInt(),
                    (-(bitmap.width * bigScale - width)).toInt() / 2,
                    (bitmap.width * bigScale - width).toInt() / 2,
                    (-(bitmap.height * bigScale - height)).toInt() / 2,
                    (bitmap.height * bigScale - height).toInt() / 2,
                )
                // 这里只触发一次，所以还需要额外操作
                postOnAnimation(FlingRunner())
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    inner class FlingRunner : Runnable {
        override fun run() {
            if (overScroller.computeScrollOffset()) {
                // 抛掷动画还在执行
                offsetX = overScroller.currX.toFloat()
                offsetY = overScroller.currY.toFloat()
                invalidate()
                // 下一帧动画的时候执行
                postOnAnimation(this)
            }
        }
    }

    inner class PhotoScaleGestureListener : ScaleGestureDetector.OnScaleGestureListener {
        private var initialScale: Float = 0f

        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            if (currentScale > smallScale && !isEnlarge || currentScale == smallScale && !isEnlarge) {
                isEnlarge = !isEnlarge
            }
            // 缩放因子
            currentScale = initialScale * detector!!.scaleFactor
            if (currentScale > bigScale) currentScale = bigScale
            if (currentScale < smallScale) currentScale = smallScale
            invalidate()
            return false
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            initialScale = currentScale
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
        }

    }
}