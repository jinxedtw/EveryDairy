package com.tw.longerrelationship.views.widgets

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import com.tw.longerrelationship.R
import com.tw.longerrelationship.views.activity.BrowserActivity
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

class ClipPopup(context: Context) : BasePopupWindow(context) {

    var mTvLink: TextView? = null
    var mIvClose: ImageView? = null

    init {
        setContentView(R.layout.layout_clip_popup)
        setBackgroundColor(Color.TRANSPARENT)
        setOutSideDismiss(false)
        isOutSideTouchable = true
    }

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)

        mTvLink = findViewById(R.id.tv_link)
        mIvClose = findViewById(R.id.iv_close)
    }

    fun showPopupWindow(anchorView: View?, clipLink: String) {
        if (isShowing) {
            contentView.setOnClickListener {
                BrowserActivity.openBrowserActivity(context, "啦啦啦", clipLink)
            }
        } else {
            super.showPopupWindow(anchorView)
            contentView.setOnClickListener {
                BrowserActivity.openBrowserActivity(context, "啦啦啦", clipLink)
            }
        }

        mTvLink?.text = clipLink
        mIvClose?.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation().withTranslation(TranslationConfig.FROM_RIGHT).toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation().withTranslation(TranslationConfig.TO_RIGHT).toDismiss()
    }
}