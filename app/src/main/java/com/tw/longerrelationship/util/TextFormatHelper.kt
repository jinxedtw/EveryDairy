package com.tw.longerrelationship.util

object TextFormatHelper {

    fun formatKeyWordColor(key: String, text: String): String =
        text.replace(key, "<font color=#f56d66>${key}</font>")
}