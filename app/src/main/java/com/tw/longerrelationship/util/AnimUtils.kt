package com.tw.longerrelationship.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.animation.DecelerateInterpolator


inline fun startValueAnim(
    from: Int, to: Int, listener: AnimatorUpdateListener?,
    duration: Long = 300L,
    crossinline onAnimStart: (Animator?) -> Unit = { _ -> },
    crossinline onAnimEnd: (Animator?) -> Unit = { _ -> },
    crossinline onAnimCancel: (Animator?) -> Unit = { _ -> },
    crossinline onAnimRepeat: (Animator?) -> Unit = { _ -> }
) {
    val animator = ValueAnimator.ofInt(from, to)
    animator.duration = duration
    animator.interpolator = DecelerateInterpolator()
    animator.addUpdateListener(listener)
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            onAnimStart.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animator?) {
            onAnimEnd.invoke(animation)
        }

        override fun onAnimationCancel(animation: Animator?) {
            onAnimCancel.invoke(animation)
        }

        override fun onAnimationRepeat(animation: Animator?) {
            onAnimRepeat.invoke(animation)
        }

    })
    animator.start()
}

