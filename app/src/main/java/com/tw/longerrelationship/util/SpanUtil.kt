package com.tw.longerrelationship.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*

object SpanUtil {
    //设置字体颜色,参数如getResources().getColor(R.color.colorBlue)
    fun foreGroundColorSpan(content: String, start: Int, end: Int, colorId: Int): SpannableStringBuilder {
        var end = end
        if (end > content.length) end = content.length
        val ssb = SpannableStringBuilder(content)
        ssb.setSpan(ForegroundColorSpan(colorId), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ssb
    }

    //在字体中的start和end之间插入图片,并把start给覆盖掉
    fun drawableSpan(content: String?, start: Int, end: Int, drawable: Drawable): SpannableStringBuilder {
        drawable.setBounds(0, 0, 50, 50)
        val spannableString = SpannableStringBuilder(content)
        spannableString.setSpan(CenterSpaceImageSpan(drawable, 5, 5), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    //设置背景,color为背景颜色,参数如getResources().getColor(R.color.colorBlue)
    fun backgroundColorSpan(content: String, start: Int, end: Int, colorId: Int): SpannableStringBuilder {
        var end = end
        if (end > content.length) end = content.length
        val ssb = SpannableStringBuilder(content)
        ssb.setSpan(BackgroundColorSpan(colorId), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        return ssb
    }

    //设置字体大小
    fun absoluteSizeSpan(content: String, start: Int, end: Int, size: Int): SpannableStringBuilder {
        var end = end
        if (end > content.length) end = content.length
        val ssb = SpannableStringBuilder(content)
        ssb.setSpan(AbsoluteSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ssb
    }

    //三种字体
    const val MONOSPACE = 0
    const val SERIF = 1
    const val SANS_SERIF = 2

    //设置字体类型
    fun typeFaceSpan(content: String, start: Int, end: Int, TypeFace: Int): SpannableStringBuilder {
        var end = end
        if (end > content.length) end = content.length
        val ssb = SpannableStringBuilder(content)
        when (TypeFace) {
            MONOSPACE -> ssb.setSpan(TypefaceSpan("monospace"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            SERIF -> ssb.setSpan(TypefaceSpan("serif"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            SANS_SERIF -> ssb.setSpan(TypefaceSpan("sans_serif"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return ssb
    }

    //设置下划线
    fun underLineSpan(content: String, start: Int, end: Int): SpannableStringBuilder {
        var end = end
        if (end > content.length) end = content.length
        val ssb = SpannableStringBuilder(content)
        ssb.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ssb
    }

    //设置删除线
    fun strikethroughSpan(content: String, start: Int, end: Int): SpannableStringBuilder {
        var end = end
        if (end > content.length) end = content.length
        val ssb = SpannableStringBuilder(content)
        ssb.setSpan(StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ssb
    }

    //设置字段开头圆点效果,color为圆点颜色,参数如getResources().getColor(R.color.colorBlue)
    fun bulletSpan(content: String, start: Int, end: Int, colorId: Int): SpannableStringBuilder {
        var end = end
        if (end > content.length) end = content.length
        val ssb = SpannableStringBuilder(content)
        ssb.setSpan(BulletSpan(10, colorId), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        return ssb
    }

    //为了图片的插入而加入的一个工具类
    class CenterSpaceImageSpan @JvmOverloads constructor(
        drawable: Drawable?,
        private val mMarginLeft: Int = 0, private val mMarginRight: Int = 0
    ) : ImageSpan(
        drawable!!
    ) {
        override fun draw(
            canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float,
            top: Int, y: Int, bottom: Int,
            paint: Paint
        ) {
            var x = x
            val b = drawable
            val fm = paint.fontMetricsInt
            x += mMarginLeft
            val transY = (y + fm.descent + y + fm.ascent) / 2 - b.bounds.bottom / 2
            canvas.save()
            canvas.translate(x, transY.toFloat())
            b.draw(canvas)
            canvas.restore()
        }

        override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?): Int {
            return mMarginLeft + super.getSize(paint, text, start, end, fm) + mMarginRight
        }
    }
}