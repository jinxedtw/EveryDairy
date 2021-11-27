package com.tw.longerrelationship.views.widgets

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.OverScroller
import androidx.annotation.Keep
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min

class PhotoView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)         // 用于初始化前占位,可以替换成loading图
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

    private var originalOffsetX = 0f        // 中心点
    private var originalOffsetY = 0f
    private var offsetX = 0f                // 水平偏移距离
    private var offsetY = 0f                // 竖直偏移距离
    private var lastX = 0f                  // 上一次的X坐标
    private var lastY = 0f                  // 上一次的Y坐标

    private var lastScrollY = 0f
    private var lastScrollX = 0f

    var onImageExit: () -> Unit = {}
    var onAlphaChange: (Float) -> Unit = {}


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
        canvas.drawBitmap(bitmap, originalOffsetX, originalOffsetY, paint)              // 画到画布中间位置
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

    /** 解决与viewPager的事件冲突 */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val x = event.rawX                  // 屏幕坐标系
        val y = event.rawY

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                // 定义滑动规则
                val deltaX = x - lastX
                val deltaY = y - lastY
                if (translationY != 0f) {
                    // picture正在平移中，不允许拦截事件
                    parent.requestDisallowInterceptTouchEvent(true)
                    return super.dispatchTouchEvent(event)
                }

                if (!isEnlarge) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    if (abs(deltaX) > abs(deltaY)) {
                        // 左右滑动
                        if (deltaX < 0) {
                            // 左
                            Log.d("事件冲突", "左滑了,x=${x},y=${y},lastX=${lastX},lastY=${lastY}")
                            if (offsetX == -(bitmap.width * bigScale - width) / 2) {
                                parent.requestDisallowInterceptTouchEvent(false)
                                Log.d("事件冲突", "左滑让事件给父亲")
                            }
                        } else {
                            // 右
                            Log.d("事件冲突", "右滑了,x=${x},y=${y},lastX=${lastX},lastY=${lastY}")
                            if (offsetX == (bitmap.width * bigScale - width) / 2) {
                                parent.requestDisallowInterceptTouchEvent(false)
                                Log.d("事件冲突", "右滑让事件给父亲")
                            }
                        }
                    }
                }
            }
        }
        lastX = x
        lastY = y
        return super.dispatchTouchEvent(event)
    }

    /** 接管Touch事件*/
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var consume: Boolean
        if (!overScroller.isFinished) {
            overScroller.forceFinished(true)
        }

        if (event.action == MotionEvent.ACTION_UP) {
            if (translationY / height > SCROLL_INTERVAL) {
                onImageExit.invoke()
            }
            if (translationY != 0f) {
                // 重置
                currentScale = smallScale
                translationY = 0f
                translationX = 0f
            }
        }

        // 双指操作优先
        consume = scaleGestureDetector.onTouchEvent(event)
        if (!scaleGestureDetector.isInProgress) {
            consume = gestureDetector.onTouchEvent(event)
        }
        return consume
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
            return true
        }

        /** 滑动 */
        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (isEnlarge) {
                offsetX -= distanceX
                offsetY -= distanceY
                fixOffsets()
                invalidate()
            } else {
                // 在没放大的情况下
                val scrollDeltaY = e2.rawY - lastScrollY
                val scrollDeltaX = e2.rawX - lastScrollX
                val translationY = this@PhotoView.translationY + scrollDeltaY
                val translationX = this@PhotoView.translationX + scrollDeltaX
                if (lastScrollY != 0f) {
                    currentScale *= (1 - (scrollDeltaY / height))
                    Log.d("滑动事件", "height:${height},translation:${translationY},height/translation:${translationY / height}")
                    Log.d("滑动事件", "$currentScale")
                    onAlphaChange(1 - (this@PhotoView.translationY / height))
                    this@PhotoView.translationY = translationY
                    this@PhotoView.translationX = translationX
                }
                lastScrollY = e2.rawY
                lastScrollX = e2.rawX
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

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            performClick()
            return super.onSingleTapConfirmed(e)
        }

        /** 这里必须要返回true,表示接收此事件序列,不然无法收到后面的ACTION_UP和ACTION_DOWN事件 */
        override fun onDown(e: MotionEvent?): Boolean {
            lastScrollX = 0f
            lastScrollY = 0f
            return true
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

    companion object {
        /** 下滑的临界值  0.1表示滑动高度占布局高度的比例 */
        private const val SCROLL_INTERVAL = 0.1
    }
}