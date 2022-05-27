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

        webSettings.javaScriptEnabled = true
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.setNeedInitialFocus(true)

        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        webSettings.setSupportMultipleWindows(true)
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        webSettings.textZoom = 100
        webSettings.defaultFontSize = 16
        webSettings.minimumFontSize = 12

        webSettings.databaseEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE)

        webSettings.setGeolocationEnabled(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webSettings.loadsImagesAutomatically = true
        webSettings.blockNetworkImage = false // 是否阻塞加载网络图片
        webSettings.allowFileAccess = true // 允许加载本地文件
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

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