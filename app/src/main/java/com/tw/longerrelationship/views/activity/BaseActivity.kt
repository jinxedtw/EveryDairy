package com.tw.longerrelationship.views.activity

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.tw.longerrelationship.util.logD
import com.tw.longerrelationship.util.setAndroidNativeLightStatusBar

abstract class BaseActivity : AppCompatActivity() {

    /**
     * 日志输出标志
     */
    val tag: String = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logD(tag, "onCreate()")

        // 设置无标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setAndroidNativeLightStatusBar(this,true)

        init()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        logD(tag, "onSaveInstanceState()")
    }



    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        logD(tag, "onRestoreInstanceState()")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        logD(tag, "onNewIntent()")
    }

    override fun onRestart() {
        super.onRestart()
        logD(tag, "onRestart()")
    }

    override fun onStart() {
        super.onStart()
        logD(tag, "onStart()")
    }

    override fun onResume() {
        super.onResume()
        logD(tag, "onResume()")
    }

    override fun onPause() {
        super.onPause()
        logD(tag, "onPause()")
    }

    override fun onStop() {
        super.onStop()
        logD(tag, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        logD(tag, "onDestroy()")
    }

    /**
     * 子类可以通过重写该方法进行一些初始化操作
     */
    abstract fun init()


    /**
     * 所有的子类必须重写该方法获取到布局的Id值
     */
    abstract fun getLayoutId(): Int

}