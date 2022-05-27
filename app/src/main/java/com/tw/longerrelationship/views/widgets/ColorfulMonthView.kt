package com.tw.longerrelationship.views.widgets

import android.content.Context
import android.graphics.Canvas
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.MonthView

class ColorfulMonthView(context: Context?) : MonthView(context) {
    private var mRadius = 0
    override fun onPreviewHook() {
        mRadius = mItemWidth.coerceAtMost(mItemHeight) / 5 * 2
    }

    override fun onDrawSelected(canvas: Canvas, calendar: Calendar, x: Int, y: Int, hasScheme: Boolean): Boolean {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mSelectedPaint)
        return true
    }

    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mSchemePaint)
    }

    override fun onDrawText(canvas: Canvas, calendar: Calendar, x: Int, y: Int, hasScheme: Boolean, isSelected: Boolean) {
        val cx = x + mItemWidth / 2
        val top = y - mItemHeight / 8
        when {
            isSelected -> {
                canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                    if (calendar.isCurrentDay) mCurDayTextPaint else mSelectTextPaint
                )
                canvas.drawText(
                    calendar.lunar, cx.toFloat(), mTextBaseLine + y + mItemHeight / 10,
                    if (calendar.isCurrentDay) mCurDayLunarTextPaint else mSelectedLunarTextPaint
                )
            }
            hasScheme -> {
                canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                    if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mSchemeTextPaint else mOtherMonthTextPaint
                )
                canvas.drawText(calendar.lunar, cx.toFloat(), mTextBaseLine + y + mItemHeight / 10, mSchemeLunarTextPaint)
            }
            else -> {
                canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                    if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mCurMonthTextPaint else mOtherMonthTextPaint
                )
                canvas.drawText(
                    calendar.lunar, cx.toFloat(), mTextBaseLine + y + mItemHeight / 10,
                    if (calendar.isCurrentDay) mCurDayLunarTextPaint else mCurMonthLunarTextPaint
                )
            }
        }
    }
}