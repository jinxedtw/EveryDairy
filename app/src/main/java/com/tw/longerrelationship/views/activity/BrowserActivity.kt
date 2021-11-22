package com.yl.qrscanner.drawer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.yl.qrscanner.base.constants.HttpUrlConstants
import com.yl.qrscanner.base.util.WebViewInitHelper
import com.yl.qrscanner.databinding.ActivityBrowserBinding
import com.yl.qrscanner.ui.ToolbarCommonActivity

/** 网页跳转 */
class BrowserToolbarActivity : ToolbarCommonActivity() {
    companion object {
        private const val EXTRA_BROWSER_TITLE = "browser_title"
        private const val EXTRA_BROWSER_URL = "browser_url"

        /**
         *  @param title 标题
         *  @param url 网站地址
         */
        fun openBrowserActivity(activity: Activity, title: String, url: String) {
            runCatching {
                val intent = Intent(activity, BrowserToolbarActivity::class.java).apply {
                    putExtra(EXTRA_BROWSER_TITLE, title)
                    putExtra(EXTRA_BROWSER_URL, url)
                }
                activity.startActivity(intent)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private lateinit var binding: ActivityBrowserBinding
    private val websiteTitle: String by lazy {
        intent.getStringExtra(EXTRA_BROWSER_TITLE) ?: ""
    }
    private val websiteUrl: String by lazy {
        intent.getStringExtra(EXTRA_BROWSER_URL) ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        loadUrl()
    }

    private fun initView() {
        title = websiteTitle

        binding.webView.apply {
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = false
        }
        WebViewInitHelper.init(binding.webView, null, WebChromeClientImpl())
    }

    private fun loadUrl() {
        if (TextUtils.isEmpty(websiteUrl)) {
            return
        }
        binding.webView.loadUrl(websiteUrl)
    }

    inner class WebChromeClientImpl : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (newProgress == 100) {
                binding.progressBar.visibility = View.GONE
                return
            }

            binding.progressBar.progress = newProgress
        }
    }
}