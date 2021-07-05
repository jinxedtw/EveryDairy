package com.tw.longerrelationship.logic.network

/**
 * 负责整个App的网络请求
 * 创建出所有的网络接口的代理类
 */
object TotalNetwork {
    var appRequestService = ServiceCreator.create(AppRequestService::class.java)
}