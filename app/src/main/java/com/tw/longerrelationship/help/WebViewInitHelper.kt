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
        webSettings.savePassword = false
        webSettings.setGeolocationEnabled(false)

        // 安全
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 适配5.0不允许http和https混合使用情况
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        webSettings.loadsImagesAutomatically = true
        webSettings.blockNetworkImage = false // 是否阻塞加载网络图片
        webSettings.allowFileAccess = true // 允许加载本地文件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.allowFileAccessFromFileURLs = false // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
        }
        webSettings.allowUniversalAccessFromFileURLs = false // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        } else {
            webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        }

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