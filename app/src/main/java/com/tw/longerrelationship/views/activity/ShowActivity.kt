package com.tw.longerrelationship.views.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tw.longerrelationship.R

class ShowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)
    }

    companion object {
        @JvmField
        var staticFiled: Int? = null
    }
}