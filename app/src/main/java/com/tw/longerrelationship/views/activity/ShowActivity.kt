package com.tw.longerrelationship.views.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.tw.longerrelationship.R

class ShowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        LayoutInflater.from(this).inflate(R.layout.activity_show, findViewById(R.id.fl_content))
    }

    companion object {
        @JvmField
        var staticFiled: Int? = null
    }
}