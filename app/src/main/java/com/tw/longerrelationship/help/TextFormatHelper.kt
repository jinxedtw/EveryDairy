package com.tw.longerrelationship.help

object TextFormatHelper {

    fun formatKeyWordColor(key: String, text: String): String =
        text.replace(key, "<font color=#f56d66>${key}</font>")
}