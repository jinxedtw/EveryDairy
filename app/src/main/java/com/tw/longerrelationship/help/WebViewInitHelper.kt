package com.tw.longerrelationship.help

import android.os.Build
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tw.longerrelationship.BuildConfig

object WebViewInitHelper {
    fun init(webView: WebView, webViewClient: WebViewClient?, webChromeClient: WebChromeClient?) {
        val webSettings = webView.settings

        // 基础
        webSettings.javaScriptEnabled = true
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.setNeedInitialFocus(true)

        // 缩放的控制
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        // 多窗口
        webSettings.setSupportMultipleWindows(true)
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        // 视觉 - 缩放
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        // 视觉 - 文字
        webSettings.textZoom = 100
        webSettings.defaultFontSize = 16
        webSettings.minimumFontSize = 12

        // 存储
        webSettings.databaseEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE)

        // 隐私
        webSettings.setGeolocationEnabled(false)

        // 安全
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 适配5.0不允许http和https混合使用情况
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webSettings.loadsImagesAutomatically = true
        webSettings.blockNetworkImage = false // 是否阻塞加载网络图片
        webSettings.allowFileAccess = true // 允许加载本地文件
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        // client
        if (webViewClient != null) {
            webView.webViewClient = webViewClient
        }
        if (webChromeClient != null) {
            webView.webChromeClient = webChromeClient
        }
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }
}