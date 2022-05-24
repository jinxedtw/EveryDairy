package com.tw.longerrelationship.views.widgets.doodle

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.tw.longerrelationship.util.savePicToAlbum

/**
 * 自定义的用于涂鸦的 View
 */
class DoodleView : SurfaceView, SurfaceHolder.Callback {
    private var mSurfaceHolder: SurfaceHolder? = null

    // 当前所选画笔的形状
    private lateinit var curAction: BaseAction

    // 默认画笔为黑色
    private var currentColor = Color.BLACK

    // 画笔的粗细
    private var currentSize = 5
    private var mPaint: Paint? = null
    private lateinit var mBaseActions: MutableList<BaseAction>
    private lateinit var mBitmap: Bitmap
    private var mActionType = ActionType.Path

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        mSurfaceHolder = this.holder
        mSurfaceHolder?.addCallback(this)
        this.isFocusable = true
        mPaint = Paint()
        mPaint!!.color = Color.WHITE
        mPaint!!.strokeWidth = currentSize.toFloat()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val canvas = mSurfaceHolder!!.lockCanvas()
        canvas.drawColor(Color.WHITE)
        mSurfaceHolder!!.unlockCanvasAndPost(canvas)
        mBaseActions = ArrayList()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {}
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_CANCEL) {
            return false
        }
        val touchX = event.x
        val touchY = event.y
        when (action) {
            MotionEvent.ACTION_DOWN -> setCurAction(touchX, touchY)
            MotionEvent.ACTION_MOVE -> {
                val canvas = mSurfaceHolder!!.lockCanvas()
                canvas.drawColor(Color.WHITE)
                for (baseAction in mBaseActions) {
                    baseAction.draw(canvas)
                }
                curAction.move(touchX, touchY)
                curAction.draw(canvas)
                mSurfaceHolder!!.unlockCanvasAndPost(canvas)
            }
            MotionEvent.ACTION_UP -> {
                mBaseActions.add(curAction)
            }
            else -> {}
        }
        return true
    }

    /**
     * 得到当前画笔的类型，并进行实例化
     *
     * @param x
     * @param y
     */
    private fun setCurAction(x: Float, y: Float) {
        when (mActionType) {
            ActionType.Point -> curAction = MyPoint(x, y, currentColor)
            ActionType.Path -> curAction = MyPath(x, y, currentSize, currentColor)
            ActionType.Line -> curAction = MyLine(x, y, currentSize, currentColor)
            ActionType.Rect -> curAction = MyRect(x, y, currentSize, currentColor)
            ActionType.Circle -> curAction = MyCircle(x, y, currentSize, currentColor)
            ActionType.FillEcRect -> curAction = MyFillRect(x, y, currentSize, currentColor)
            ActionType.FilledCircle -> curAction = MyFillCircle(x, y, currentSize, currentColor)
        }
    }

    /**
     * 设置画笔的颜色
     *
     * @param color 颜色
     */
    fun setColor(color: String?) {
        currentColor = Color.parseColor(color)
    }

    /**
     * 设置画笔的粗细
     *
     * @param size 画笔的粗细
     */
    fun setSize(size: Int) {
        currentSize = size
    }

    /**
     * 设置画笔的形状
     *
     * @param type 画笔的形状
     */
    fun setType(type: ActionType) {
        mActionType = type
    }

    /**
     * 将当前的画布转换成一个 Bitmap
     *
     * @return Bitmap
     */
    val bitmap: Bitmap
        get() {
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(mBitmap)
            doDraw(canvas)
            return mBitmap
        }

    /**
     * 保存涂鸦后的图片
     */
    fun saveBitmap() {
        context.savePicToAlbum(bitmap)
    }

    /**
     * 开始进行绘画
     *
     * @param canvas
     */
    private fun doDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        for (action in mBaseActions) {
            action.draw(canvas)
        }
        canvas.drawBitmap(mBitmap, 0f, 0f, mPaint)
    }

    /**
     * 回退
     *
     * @return 是否已经回退成功
     */
    fun back(): Boolean {
        if (mBaseActions.size > 0) {
            mBaseActions.removeAt(mBaseActions.size - 1)
            val canvas = mSurfaceHolder!!.lockCanvas()
            canvas.drawColor(Color.WHITE)
            for (action in mBaseActions) {
                action.draw(canvas)
            }
            mSurfaceHolder!!.unlockCanvasAndPost(canvas)
            return true
        }
        return false
    }

    /**
     * 重置签名
     */
    fun reset() {
        if (mBaseActions.size > 0) {
            mBaseActions.clear()
            val canvas = mSurfaceHolder!!.lockCanvas()
            canvas.drawColor(Color.WHITE)
            for (action in mBaseActions) {
                action.draw(canvas)
            }
            mSurfaceHolder!!.unlockCanvasAndPost(canvas)
        }
    }

    enum class ActionType {
        Point, Path, Line, Rect, Circle, FillEcRect, FilledCircle
    }
}