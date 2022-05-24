package com.tw.longerrelationship.views.widgets.doodle

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

/**
 * 动作的基础类
 */
internal abstract class BaseAction {
    var color: Int

    constructor() {
        color = Color.WHITE
    }

    constructor(color: Int) {
        this.color = color
    }

    abstract fun draw(canvas: Canvas)
    abstract fun move(mx: Float, my: Float)
}

internal class MyPoint(private val x: Float, private val y: Float, color: Int) : BaseAction(color) {
    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = color
        canvas.drawPoint(x, y, paint)
    }

    override fun move(mx: Float, my: Float) {}
}

/**
 * 自由曲线
 */
internal class MyPath : BaseAction {
    private var path: Path
    private var size: Int

    constructor() {
        path = Path()
        size = 1
    }

    constructor(x: Float, y: Float, size: Int, color: Int) : super(color) {
        path = Path()
        this.size = size
        path.moveTo(x, y)
        path.lineTo(x, y)
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.isDither = true
        paint.color = color
        paint.strokeWidth = size.toFloat()
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawPath(path, paint)
    }

    override fun move(mx: Float, my: Float) {
        path.lineTo(mx, my)
    }
}

/**
 * 直线
 */
internal class MyLine : BaseAction {
    private var startX: Float
    private var startY: Float
    private var stopX: Float
    private var stopY: Float
    private var size = 0

    constructor() {
        startX = 0f
        startY = 0f
        stopX = 0f
        stopY = 0f
    }

    constructor(x: Float, y: Float, size: Int, color: Int) : super(color) {
        startX = x
        startY = y
        stopX = x
        stopY = y
        this.size = size
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = color
        paint.strokeWidth = size.toFloat()
        canvas.drawLine(startX, startY, stopX, stopY, paint)
    }

    override fun move(mx: Float, my: Float) {
        stopX = mx
        stopY = my
    }
}

/**
 * 方框
 */
internal class MyRect : BaseAction {
    private var startX: Float
    private var startY: Float
    private var stopX: Float
    private var stopY: Float
    private var size = 0

    constructor() {
        startX = 0f
        startY = 0f
        stopX = 0f
        stopY = 0f
    }

    constructor(x: Float, y: Float, size: Int, color: Int) : super(color) {
        startX = x
        startY = y
        stopX = x
        stopY = y
        this.size = size
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = color
        paint.strokeWidth = size.toFloat()
        canvas.drawRect(startX, startY, stopX, stopY, paint)
    }

    override fun move(mx: Float, my: Float) {
        stopX = mx
        stopY = my
    }
}

/**
 * 圆框
 */
internal class MyCircle : BaseAction {
    private var startX: Float
    private var startY: Float
    private var stopX: Float
    private var stopY: Float
    private var radius: Float
    private var size = 0

    constructor() {
        startX = 0f
        startY = 0f
        stopX = 0f
        stopY = 0f
        radius = 0f
    }

    constructor(x: Float, y: Float, size: Int, color: Int) : super(color) {
        startX = x
        startY = y
        stopX = x
        stopY = y
        radius = 0f
        this.size = size
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = color
        paint.strokeWidth = size.toFloat()
        canvas.drawCircle((startX + stopX) / 2, (startY + stopY) / 2, radius, paint)
    }

    override fun move(mx: Float, my: Float) {
        stopX = mx
        stopY = my
        radius = (Math.sqrt(
            ((mx - startX) * (mx - startX)
                    + (my - startY) * (my - startY)).toDouble()
        ) / 2).toFloat()
    }
}

internal class MyFillRect : BaseAction {
    private var startX: Float
    private var startY: Float
    private var stopX: Float
    private var stopY: Float
    private var size = 0

    constructor() {
        startX = 0f
        startY = 0f
        stopX = 0f
        stopY = 0f
    }

    constructor(x: Float, y: Float, size: Int, color: Int) : super(color) {
        startX = x
        startY = y
        stopX = x
        stopY = y
        this.size = size
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = color
        paint.strokeWidth = size.toFloat()
        canvas.drawRect(startX, startY, stopX, stopY, paint)
    }

    override fun move(mx: Float, my: Float) {
        stopX = mx
        stopY = my
    }
}

/**
 * 圆饼
 */
internal class MyFillCircle(private val startX: Float, private val startY: Float, size: Int, color: Int) : BaseAction(color) {
    private var stopX: Float
    private var stopY: Float
    private var radius: Float
    private val size: Int
    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = color
        paint.strokeWidth = size.toFloat()
        canvas.drawCircle((startX + stopX) / 2, (startY + stopY) / 2, radius, paint)
    }

    override fun move(mx: Float, my: Float) {
        stopX = mx
        stopY = my
        radius = (Math.sqrt(
            ((mx - startX) * (mx - startX)
                    + (my - startY) * (my - startY)).toDouble()
        ) / 2).toFloat()
    }

    init {
        stopX = startX
        stopY = startY
        radius = 0f
        this.size = size
    }
}