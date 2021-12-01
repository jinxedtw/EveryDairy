package com.tw.longerrelationship.test

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.LiveDataBus
import com.tw.longerrelationship.util.showToast

class ShowActivity : AppCompatActivity() {

    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var text3: TextView
    private lateinit var textAdapter: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)

        initView()
    }

    private fun initView() {

        text1 = findViewById(R.id.text_1)

        LiveDataBus.with("啦啦啦",String::class.java).value="哇哇哇哇哇"

        text1.setOnClickListener {
            val intent=Intent(this, TestActivity::class.java).apply {
                val data2 = ByteArray(1024 * 512)
                putExtra("111",data2)
            }
            startActivity(intent)
        }

    }

    companion object {
        @JvmField
        var staticFiled: Int? = null
    }
}