package com.tw.longerrelationship.logic.network

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