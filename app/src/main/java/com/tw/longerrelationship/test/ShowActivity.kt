package com.tw.longerrelationship.views.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tw.longerrelationship.R

class ShowActivity : AppCompatActivity() {
    //    private lateinit var tv0: TextView
//    private lateinit var tv1: TextView
//    private lateinit var tv2: TextView
//    private lateinit var tv3: TextView
//    private lateinit var tv4: TextView
//    private lateinit var tv5: TextView
//    private lateinit var tv6: TextView
//    private lateinit var tv7: TextView
//    private lateinit var tv8: TextView
//    private lateinit var tv9: TextView
//    private lateinit var tv10: TextView
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
//        tv0 = findViewById(R.id.tv_0)
//        tv1 = findViewById(R.id.tv_1)
//        tv2 = findViewById(R.id.tv_2)
//        tv3 = findViewById(R.id.tv_3)
//        tv4 = findViewById(R.id.tv_4)
//        tv5 = findViewById(R.id.tv_5)
//        tv6 = findViewById(R.id.tv_6)
//        tv7 = findViewById(R.id.tv_7)
//        tv8 = findViewById(R.id.tv_8)
//        tv9 = findViewById(R.id.tv_9)
//        tv10 = findViewById(R.id.tv_10)
        text1 = findViewById(R.id.text_1)

        
        text1.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
//        tv1.paint.style = Paint.Style.FILL_AND_STROKE
//        tv1.paint.strokeWidth = 0f
//
//        tv2.paint.style = Paint.Style.FILL_AND_STROKE
//        tv2.paint.strokeWidth = 0.2f
//
//        tv3.paint.style = Paint.Style.FILL_AND_STROKE
//        tv3.paint.strokeWidth = 0.4f
//
//        tv4.paint.style = Paint.Style.FILL_AND_STROKE
//        tv4.paint.strokeWidth = 0.6f
//
//        tv5.paint.style = Paint.Style.FILL_AND_STROKE
//        tv5.paint.strokeWidth = 0.8f
//
//        tv6.paint.style = Paint.Style.FILL_AND_STROKE
//        tv6.paint.strokeWidth = 1f
//
//        tv7.paint.style = Paint.Style.FILL_AND_STROKE
//        tv7.paint.strokeWidth = 1.5f
//
//        tv8.paint.style = Paint.Style.FILL_AND_STROKE
//        tv8.paint.strokeWidth = 2f
//
//        tv9.paint.style = Paint.Style.FILL_AND_STROKE
//        tv9.paint.strokeWidth = 2.5f
//
//        tv10.paint.style = Paint.Style.FILL_AND_STROKE
//        tv10.paint.strokeWidth = 5f

//        text2.setMiddleWeight()
//
//        text3.setBoldWeight()

//        textAdapter.post {
//            val a = textAdapter.width
//            val b = textAdapter.height
//            logD("testaaaa", "${textAdapter.width}+${textAdapter.height}")
//        }
    }

    companion object {
        @JvmField
        var staticFiled: Int? = null
    }
}