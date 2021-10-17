package com.tw.longerrelationship.views.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.IBinder
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.dp2px
import com.tw.longerrelationship.util.logD
import com.tw.longerrelationship.util.setAndroidNativeLightStatusBar
import com.tw.longerrelationship.util.setDrawable


abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {

    /**
     * 日志输出标志
     */
    protected val tag: String = this.javaClass.simpleName

    /**
     * 根布局
     */
    protected var root: View? = null
    protected lateinit var mBinding: T
    private lateinit var mAppBar: ConstraintLayout
    private lateinit var mLayoutContent: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logD(tag, "onCreate()")

        // 设置无标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setAndroidNativeLightStatusBar(this, true)
        setTouchListener(window.decorView)
        init()
    }

    /**
     * 初始化Binding
     * 有普通初始化[initBinding]和带appBar两种初始化方式 [initBindingWithAppBar]
     */
    fun initBinding() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        root = mBinding.root
    }

    /**
     * 设置有统一appBar样式的布局
     */
    fun initBindingWithAppBar() {
        setContentView(R.layout.activity_base)
        mAppBar = findViewById(R.id.cl_app_bar)
        mLayoutContent = findViewById(R.id.fl_content)
        mBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), mLayoutContent, true)
        findViewById<ImageView>(R.id.iv_leave)?.setOnClickListener { finishAndTryCloseSoftKeyboard() }
        root = mBinding.root
    }

    fun setAppBarTitle(title: String) {
        findViewById<TextView>(R.id.tv_appbar_title).text = title
    }

    fun setAppBarRightText(text: String, onClick: View.OnClickListener?) {
        findViewById<TextView>(R.id.tv_right).apply {
            this.text = text
            this.visibility = View.VISIBLE
            setOnClickListener(onClick)
        }
    }

    fun setAppBarRightImage(@DrawableRes res: Int, onClick: View.OnClickListener?) {
        findViewById<ImageView>(R.id.iv_right).apply {
            setDrawable(res)
            setOnClickListener(onClick)
        }
    }

    /**
     * 自定义appbar右边布局
     * 按照装入顺着从右到左摆放在appbar中
     *
     * todo 该方法有问题，会导致views无法显示，怀疑   API constraintSet.applyTo(mAppBar)
     */
    fun setAppBarRightLayout(vararg views: View) {
        var lastViewId = 0
        val constraintSet = ConstraintSet()
        constraintSet.clone(mAppBar)
        views.forEach {
            val params: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            it.id = View.generateViewId()
            // 等同于  layout_constraintRight_toRightOf="lastView"
            lastViewId = if (lastViewId == 0) ConstraintSet.PARENT_ID else lastViewId
            constraintSet.connect(it.id, ConstraintSet.RIGHT, lastViewId, ConstraintSet.RIGHT)
            // 左右设置10dp的padding
            it.setPadding(dp2px(10), 0, dp2px(10), 0)
            it.layoutParams = params
            lastViewId = it.id
            mAppBar.addView(it)
            constraintSet.applyTo(mAppBar)
            constraintSet.clone(mAppBar)
        }
    }

    protected fun finishAndTryCloseSoftKeyboard() {
        if (isSoftShowing()) {
            closeKeyboard(root!!.windowToken)
            root!!.handler.postDelayed({
                finish()
            }, 50)
        } else {
            finish()
        }
    }

    private fun setTouchListener(view: View?) {
        if (view == null) {
            return
        }
        if (view !is EditText) {
            view.setOnTouchListener(OnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        manager.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    MotionEvent.ACTION_UP -> {
                        v.performClick()
                    }
                }
                false
            })
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setTouchListener(innerView)
            }
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
        if (isSoftShowing()) {
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(windowToken, 0)
        }
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