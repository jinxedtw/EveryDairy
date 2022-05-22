package com.tw.longerrelationship.views.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import com.blankj.utilcode.util.BarUtils
import com.sevenheaven.gesturelock.GestureLock
import com.sevenheaven.gesturelock.GestureLock.MODE_NORMAL
import com.sevenheaven.gesturelock.GestureLockView
import com.sevenheaven.gesturelock.NormalStyleLockView
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivityGestureBinding
import com.tw.longerrelationship.logic.LiveDataBus
import com.tw.longerrelationship.util.Constants
import com.tw.longerrelationship.util.Constants.KEY_GESTURE_LOCK_PATH
import com.tw.longerrelationship.util.DataStoreUtil
import com.tw.longerrelationship.util.gone
import com.tw.longerrelationship.util.setOnClickListeners

class GestureLockActivity : BaseActivity<ActivityGestureBinding>() {
    private var gesturePath: IntArray = intArrayOf()
    private val gestureMode by lazy {
        intent.getIntExtra(Constants.INTENT_GESTURE_LOCK_MODE, MODE_NORMAL)
    }

    override fun init() {
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT)
        BarUtils.setStatusBarLightMode(this, false)
        initBinding()
        initView()
        initEvent()
    }

    private fun initView() {
        mBinding.gestureLock.setMode(gestureMode)

        if (gestureMode == MODE_NORMAL) {
            mBinding.tvGuide.text = "绘制图案以解锁"
            mBinding.tvCancer.gone()
            mBinding.tvContinue.gone()
            getGesturePath()
        }

        setOnClickListeners(mBinding.tvCancer, mBinding.tvContinue) {
            when (this) {
                mBinding.tvCancer -> {
                    if (gesturePath.isNotEmpty()) {
                        mBinding.tvGuide.text = "绘制解锁图案"
                        mBinding.tvCancer.text = "取消"
                        mBinding.gestureLock.setTouchable(true)
                        gesturePath = intArrayOf()
                        mBinding.gestureLock.clear()
                    } else {
                        finish()
                    }
                }
                mBinding.tvContinue -> {
                    if (gesturePath.isNotEmpty()) {
                        AlertDialog.Builder(this@GestureLockActivity).setMessage("是否确认保存图案?")
                            .setPositiveButton("确认") { _, _ ->
                                saveGesturePath()
                                LiveDataBus.with(Constants.LIVE_SET_LOCK, Boolean::class.java).postValue(true)
                                finish()
                            }
                            .setNeutralButton("取消", null)
                            .show()
                    }
                }
            }
        }

        mBinding.gestureLock.setAdapter(object : GestureLock.GestureLockAdapter {
            override fun getDepth(): Int {
                return 3
            }

            override fun getCorrectGestures(): IntArray {
                return gesturePath
            }

            // 最大可重试次数
            override fun getUnmatchedBoundary(): Int {
                return 5
            }

            // block之前的间隔大小
            override fun getBlockGapSize(): Int {
                return 5
            }

            override fun getGestureLockViewInstance(context: Context, position: Int): GestureLockView {
                return NormalStyleLockView(context)
            }
        })
    }

    private fun initEvent() {
        mBinding.gestureLock.setOnGestureEventListener(object : GestureLock.OnGestureEventListener {
            override fun onBlockSelected(position: Int) {}
            override fun onUnmatchedExceedBoundary() {}

            override fun onGestureEvent(matched: Boolean, gesturesContainer: IntArray, gestureCount: Int) {
                if (gestureMode == MODE_NORMAL) {
                    if (matched) {
                        startActivity(Intent(this@GestureLockActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        mBinding.tvGuide.text = "图案错误,请重试"
                        setAnimator(mBinding.tvGuide)
                    }
                } else {
                    if (gestureCount < 4) {
                        mBinding.tvGuide.text = "至少连接4个点,请重画"
                        mBinding.tvCancer.text = "取消"
                    } else {
                        mBinding.tvCancer.text = "重画"
                        gesturePath = gesturesContainer
                        mBinding.tvGuide.text = "已经记录图案"
                        mBinding.gestureLock.setTouchable(false)
                    }
                }
            }


            override fun onGestureMove() {
                mBinding.tvGuide.text = "完成后松开手指"
            }
        })
    }

    override fun getLayoutId(): Int = R.layout.activity_gesture

    private fun getGesturePath() {
        val path = DataStoreUtil[KEY_GESTURE_LOCK_PATH] ?: ""

        path.split("$").forEach {
            if (it.isNotEmpty() && it.toInt() != -1) {
                gesturePath = gesturePath.plus(it.toInt())
            }
        }
    }

    private fun saveGesturePath() {
        val sb = StringBuilder()
        gesturePath.forEach {
            sb.append(it)
            sb.append("$")
        }
        DataStoreUtil[KEY_GESTURE_LOCK_PATH] = sb.toString()
    }

    /**
     * 左右晃动动效
     */
    private fun setAnimator(vararg v: View?) {
        val animators = mutableListOf<Animator>()
        v.forEach {
            animators.add(
                ObjectAnimator.ofFloat(it, "translationX", 0f, -26f, 26f, -22f, 22f, -18f, 18f, -14f, 14f, -10f, 10f, -6f, 6f, -3f, 3f, -1f, 1f, 0f).apply {
                    duration = 2000
                })
        }
        AnimatorSet().apply {
            playTogether(animators)
            start()
        }
    }

    companion object {
        fun open(activity: Activity, mode: Int) {
            activity.startActivity(Intent(activity, GestureLockActivity::class.java).apply {
                putExtra(Constants.INTENT_GESTURE_LOCK_MODE, mode)
            })
        }

        fun open(context: Context, mode: Int) {
            context.startActivity(Intent(context, GestureLockActivity::class.java).apply {
                putExtra(Constants.INTENT_GESTURE_LOCK_MODE, mode)
            })
        }
    }
}