package com.tw.longerrelationship.views.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.tw.longerrelationship.R
import com.tw.longerrelationship.databinding.ActivityBrowserBinding
import com.tw.longerrelationship.help.WebViewInitHelper

/** 网页跳转 */
class BrowserActivity : BaseActivity<ActivityBrowserBinding>() {
    companion object {
        private const val EXTRA_BROWSER_TITLE = "browser_title"
        private const val EXTRA_BROWSER_URL = "browser_url"

        /**
         *  @param title 标题
         *  @param url 网站地址
         */
        fun openBrowserActivity(activity: Activity, title: String, url: String) {
            runCatching {
                val intent = Intent(activity, BrowserActivity::class.java).apply {
                    putExtra(EXTRA_BROWSER_TITLE, title)
                    putExtra(EXTRA_BROWSER_URL, url)
                }
                activity.startActivity(intent)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private val websiteTitle: String by lazy {
        intent.getStringExtra(EXTRA_BROWSER_TITLE) ?: ""
    }
    private val websiteUrl: String by lazy {
        intent.getStringExtra(EXTRA_BROWSER_URL) ?: ""
    }


    override fun init() {
        initBindingWithAppBar()
        setAppBarTitle(websiteTitle)
        initView()
        loadUrl()
    }

    override fun getLayoutId(): Int = R.layout.activity_browser

    private fun initView() {
        mBinding.webView.apply {
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = false
        }
        WebViewInitHelper.init(mBinding.webView, null, WebChromeClientImpl())
    }

    private fun loadUrl() {
        if (TextUtils.isEmpty(websiteUrl)) {
            return
        }
        mBinding.webView.loadUrl(websiteUrl)
    }

    inner class WebChromeClientImpl : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (newProgress == 100) {
                mBinding.progressBar.visibility = View.GONE
                return
            }

            mBinding.progressBar.progress = newProgress
        }
    }
}