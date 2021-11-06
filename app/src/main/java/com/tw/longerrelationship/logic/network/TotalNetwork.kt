package com.tw.longerrelationship.logic.network

/**
 * 负责整个App的网络请求
 * 创建出所有的网络接口的代理类
 */
class TotalNetwork {
    var weatherRequester = ServiceCreator.create(WeatherRequestService::class.java)
        private set

    suspend fun getNowWeather(address:String) = weatherRequester.requestNowWeather(address)

    companion object {
        private var network: TotalNetwork? = null

        fun getInstance(): TotalNetwork {
            if (network == null) {
                synchronized(TotalNetwork::class.java) {
                    if (network == null) {
                        network = TotalNetwork()
                    }
                }
            }
            return network!!
        }
    }
}