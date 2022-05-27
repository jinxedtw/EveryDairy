package com.tw.longerrelationship.help

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.tw.longerrelationship.MyApplication.Companion.appContext
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.DataStoreUtil
import com.tw.longerrelationship.views.widgets.ColorsPainDialog

object DairyColorHelper {

    val colorList = arrayListOf(
        R.color.backGroundColor1,
        R.color.backGroundColor2,
        R.color.backGroundColor3,
        R.color.backGroundColor4,
        R.color.backGroundColor5,
        R.color.backGroundColor6,
        R.color.backGroundColor7,
        R.color.backGroundColor8,
        R.color.backGroundColor9,
        R.color.backGroundColor10,
    )

    private const val MAIN_COLOR_DEPTH_EXTENT: Int = 25
    private const val ICON_COLOR_DEPTH_EXTENT: Int = 80

    fun getDairyMainColor(): Int = colorList[DataStoreUtil[ColorsPainDialog.DEFAULT_COLOR_INDEX] ?: 0]

    fun getImageSelectorAndRecoverColor(@ColorInt color: Int): Int {
        return addColorDepth(color, MAIN_COLOR_DEPTH_EXTENT)
    }

    fun getTextColor(@ColorInt color: Int): Int {
        return if (isDarkTheme(color)) {
            ContextCompat.getColor(appContext, R.color.dairy_edit_day)
        } else {
            ContextCompat.getColor(appContext, R.color.dairy_edit_dark)
        }
    }

    fun getEditContentColor(@ColorInt color: Int): Int {
        return if (isDarkTheme(color)) {
            ContextCompat.getColor(appContext, R.color.dairy_edit_content_day)
        } else {
            ContextCompat.getColor(appContext, R.color.dairy_edit_content_dark)
        }
    }

    fun getIconColor(@ColorInt color: Int): Int {
        return if (isDarkTheme(color)) {
            ContextCompat.getColor(appContext, R.color.dairy_icon_select_day)
        } else {
            ContextCompat.getColor(appContext, R.color.dairy_icon_select_dark)
        }
    }

    fun isDarkTheme(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) > 0.6
    }

    private fun addColorDepth(@ColorInt color: Int, extent: Int): Int {
        var red = color and 0xff0000 shr 16
        var green = color and 0x00ff00 shr 8
        var blue = color and 0x0000ff

        red = if (red - extent > 0) red - extent else 0
        green = if (green - extent > 0) green - extent else 0
        blue = if (blue - extent > 0) blue - extent else 0
        return Color.rgb(red, green, blue)
    }
}