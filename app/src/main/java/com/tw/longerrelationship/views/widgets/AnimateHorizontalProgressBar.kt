package com.tw.longerrelationship.views.widgets

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.*
import android.util.AttributeSet
import android.util.Log
import android.widget.ProgressBar
import androidx.core.view.GravityCompat
import com.tw.longerrelationship.R

class AnimateHorizontalProgressBar @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ProgressBar(context, attrs, 0) {
    private var mProgressAnimator: ValueAnimator? = null
    private var mMaxAnimator: ValueAnimator? = null
    private var inAnimating:Boolean = false
    private var mAnimateProgressListener: AnimateProgressListener? = null
    private fun createGradientDrawable(color: Int, radius: Int): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.setColor(color)
        gradientDrawable.cornerRadius = radius.toFloat()
        return gradientDrawable
    }

    private fun setUpAnimator() {
        mProgressAnimator = ValueAnimator()
        mProgressAnimator!!.addUpdateListener { animation: ValueAnimator -> super@AnimateHorizontalProgressBar.setProgress((animation.animatedValue as Int)) }
        mProgressAnimator!!.addListener(object : SimpleAnimatorListener() {
            override fun onAnimationStart(animation: Animator) {
                inAnimating = true
                if (mAnimateProgressListener != null) {
                    mAnimateProgressListener!!.onAnimationStart(progress, max)
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                inAnimating = false
                if (mAnimateProgressListener != null) {
                    mAnimateProgressListener!!.onAnimationEnd(progress, max)
                }
            }
        })
        mProgressAnimator!!.duration = DEFAULT_DURATION
        mMaxAnimator = ValueAnimator()
        mMaxAnimator!!.addUpdateListener { animation: ValueAnimator -> super@AnimateHorizontalProgressBar.setMax((animation.animatedValue as Int)) }
        mMaxAnimator!!.addListener(object : SimpleAnimatorListener() {
            override fun onAnimationStart(animation: Animator) {
                inAnimating = true
                if (mAnimateProgressListener != null) {
                    mAnimateProgressListener!!.onAnimationStart(progress, max)
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                inAnimating = false
                if (mAnimateProgressListener != null) {
                    mAnimateProgressListener!!.onAnimationEnd(progress, max)
                }
            }
        })
        mMaxAnimator!!.duration = DEFAULT_DURATION
    }

    fun setProgressWithAnim(progress: Int) {
        if (inAnimating) {
            Log.w(TAG, "now is animating. cant override animator")
            return
        }
        if (mProgressAnimator == null) {
            setUpAnimator()
        }
        mProgressAnimator!!.setIntValues(getProgress(), progress)
        mProgressAnimator!!.start()
    }

    @Synchronized
    override fun setProgress(progress: Int) {
        if (!inAnimating) {
            super.setProgress(progress)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mProgressAnimator != null) {
            mProgressAnimator!!.cancel()
        }
        if (mMaxAnimator != null) {
            mMaxAnimator!!.cancel()
        }
    }

    fun setMaxWithAnim(max: Int) {
        if (inAnimating) {
            Log.w(TAG, "now is animating. cant override animator")
            return
        }
        if (mMaxAnimator == null) {
            setUpAnimator()
        }
        mMaxAnimator!!.setIntValues(getMax(), max)
        mMaxAnimator!!.start()
    }

    fun cancelAnimation() {
        if (!inAnimating) {
            Log.w(TAG, "now is no animating.")
            return
        }
        if (mProgressAnimator != null) {
            mProgressAnimator!!.cancel()
        }
        if (mMaxAnimator != null) {
            mMaxAnimator!!.cancel()
        }
        inAnimating = false
    }

    @Synchronized
    override fun setMax(max: Int) {
        if (!inAnimating) {
            super.setMax(max)
        }
    }

    var animDuration: Long
        get() = mProgressAnimator!!.duration
        set(duration) {
            mProgressAnimator!!.duration = duration
            mMaxAnimator!!.duration = duration
        }

    fun setStartDelay(delay: Long) {
        mProgressAnimator!!.startDelay = delay
        mMaxAnimator!!.startDelay = delay
    }

    fun setAnimInterpolator(timeInterpolator: TimeInterpolator) {
        mProgressAnimator!!.interpolator = timeInterpolator
        mMaxAnimator!!.interpolator = timeInterpolator
    }

    fun setAnimateProgressListener(animateProgressListener: AnimateProgressListener?) {
        mAnimateProgressListener = animateProgressListener
    }

    interface AnimateProgressListener {
        fun onAnimationStart(progress: Int, max: Int)
        fun onAnimationEnd(progress: Int, max: Int)
    }

    private abstract inner class SimpleAnimatorListener : Animator.AnimatorListener {
        abstract override fun onAnimationStart(animation: Animator)
        abstract override fun onAnimationEnd(animation: Animator)
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    }

    companion object {
        private val TAG = AnimateHorizontalProgressBar::class.java.name
        private const val DEFAULT_DURATION: Long = 1000
        private const val DEFAULT_CORNER_RADIUS = -1
        private val DEFAULT_PROGRESS_COLOR = Color.parseColor("#FF0000")
        private val DEFAULT_PROGRESS_BACKGROUND_COLOR = Color.parseColor("#FFFFFF")
    }

    init {
        setUpAnimator()

        val ta = getContext().obtainStyledAttributes(attrs, R.styleable.AnimateHorizontalProgressBar)
        val progressColor = ta.getColor(R.styleable.AnimateHorizontalProgressBar_ahp_progressColor, DEFAULT_PROGRESS_COLOR)
        val backgroundColor = ta.getColor(R.styleable.AnimateHorizontalProgressBar_ahp_backgroundColor, DEFAULT_PROGRESS_BACKGROUND_COLOR)
        val cornerRadius = ta.getDimensionPixelSize(R.styleable.AnimateHorizontalProgressBar_ahp_cornerRadius, DEFAULT_CORNER_RADIUS)
        val progressClipDrawable: ClipDrawable
        val progressDrawables: Array<Drawable>
        if (cornerRadius > 0) {
            progressClipDrawable = ClipDrawable(
                createGradientDrawable(progressColor, cornerRadius),
                GravityCompat.START,
                ClipDrawable.HORIZONTAL
            )
            progressDrawables = arrayOf(
                createGradientDrawable(backgroundColor, cornerRadius),
                progressClipDrawable
            )
        } else {
            progressClipDrawable = ClipDrawable(
                ColorDrawable(progressColor),
                GravityCompat.START,
                ClipDrawable.HORIZONTAL
            )
            progressDrawables = arrayOf(
                ColorDrawable(backgroundColor),
                progressClipDrawable
            )
        }
        val progressLayerDrawable = LayerDrawable(progressDrawables)
        progressLayerDrawable.setId(0, android.R.id.background)
        progressLayerDrawable.setId(1, android.R.id.progress)
        super.setProgressDrawable(progressLayerDrawable)
        ta.recycle()
    }
}