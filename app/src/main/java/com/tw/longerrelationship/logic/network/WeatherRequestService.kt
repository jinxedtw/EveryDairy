package com.tw.longerrelationship.logic.network

import com.tw.longerrelationship.logic.model.WeatherItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 包含请求的一些接口
 *
 * @Path 作用是把参数填到Url里面去
 */
interface WeatherRequestService {
    @GET("now.json")
    suspend fun requestNowWeather(
        @Query("location") address: String,
        @Query("key") key: String = ServiceCreator.weatherApiSecreteKey,
        @Query("language") language: String = "zh-Hans",
        @Query("unit") unit: String = "c"
    ): WeatherItem?
}