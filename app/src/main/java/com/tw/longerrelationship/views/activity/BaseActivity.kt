package com.tw.longerrelationship.views.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.*


abstract class BaseActivity : AppCompatActivity() {

    /**
     * 日志输出标志
     */
    private val tag: String = this.javaClass.simpleName
    var root: View? = null                                  // 根布局

    val sharedPreferences: SharedPreferences by lazy {
        baseContext.getSharedPreferences(
            Constants.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logD(tag, "onCreate()")

        // 设置无标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setAndroidNativeLightStatusBar(this, true)

        root = init()
        setUpViews()
    }

    private fun setUpViews() {
        val leaveIcon = findViewById<ImageView>(R.id.iv_leave)
        leaveIcon?.setOnClickListener { finishAndTryCloseSoftKeyboard() }
    }

    fun finishAndTryCloseSoftKeyboard() {
        if (isSoftShowing()) {
            closeKeyboard(root!!.windowToken)
            root!!.handler.postDelayed({
                finish()
            }, 50)
        } else {
            finish()
        }
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
     * 判断软键盘是否在显示
     */
    fun isSoftShowing(): Boolean {
        //获取当前屏幕内容的高度
        val screenHeight = window.decorView.height
        //获取View可见区域的bottom
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)
        return screenHeight - rect.bottom != 0
    }

    /**
     * 关闭软键盘
     */
    fun closeKeyboard(windowToken: IBinder) {
        if (isSoftShowing())
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                windowToken,                              // 关闭软键盘
                0
            )
    }

    /**
     * 子类可以通过重写该方法进行一些初始化操作
     */
    abstract fun init(): View?


    /**
     * 所有的子类必须重写该方法获取到布局的Id值
     */
    abstract fun getLayoutId(): Int

}