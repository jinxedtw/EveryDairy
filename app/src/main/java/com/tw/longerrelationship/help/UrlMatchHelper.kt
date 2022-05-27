package com.tw.longerrelationship.help

import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.tw.longerrelationship.MyApplication.Companion.appContext
import com.tw.longerrelationship.R
import java.util.regex.Matcher
import java.util.regex.Pattern

object UrlMatchHelper {
    const val URL_REGEX = "[a-zA-z]+://[^\\s]*"


    inline fun setMatchURL(rawString: String, crossinline onClickCallBack: (String) -> Unit): SpannableString {
        val spannableString = SpannableString(rawString)

        val mat: Matcher = Pattern.compile(URL_REGEX).matcher(rawString)
        while (mat.find()) {
            if (mat.group(0).isNullOrEmpty()) continue
            val url: String = mat.group(0)!!
            val startIndex = rawString.indexOf(url)

            spannableString.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        onClickCallBack(url)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = ContextCompat.getColor(appContext, R.color.dairy_url)
                        ds.flags = Paint.UNDERLINE_TEXT_FLAG
                        ds.isAntiAlias = true
                    }
                }, startIndex, startIndex + url.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannableString
    }
}