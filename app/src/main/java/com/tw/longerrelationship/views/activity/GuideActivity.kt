package com.tw.longerrelationship.views.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.setAndroidNativeLightStatusBar
import com.tw.longerrelationship.util.setStatusBarColor

/**
 * 向导页面
 */
class GuideActivity : AppCompatActivity() {
    lateinit var icon: ImageView
    lateinit var button: Button
    lateinit var name: TextView
    lateinit var hint: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setAndroidNativeLightStatusBar(this, true)
        setStatusBarColor(ContextCompat.getColor(this,R.color.blue_app_icon))

        setContentView(R.layout.activity_guide)

        icon = findViewById<ImageView>(R.id.iv_icon).apply {
            startAnimation(AnimationUtils.loadAnimation(this@GuideActivity, R.anim.guide_icon))
        }

        name = findViewById<TextView>(R.id.tv_name).apply {
            startAnimation(getAnim().apply { startOffset = 1000 })
        }

        hint = findViewById<TextView>(R.id.tv_hint).apply {
            startAnimation(getAnim().apply { startOffset = 2000 })
        }

        button = (findViewById<Button>(R.id.bt_start)).apply {
            startAnimation(getAnim().apply { startOffset = 3000 })
            setOnClickListener {
                startActivity(Intent(this@GuideActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun getAnim(): Animation {
        return AlphaAnimation(0f, 1.0f).apply {
            duration = 2000
        }
    }
}