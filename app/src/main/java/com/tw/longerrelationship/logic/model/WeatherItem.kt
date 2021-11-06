package com.tw.longerrelationship.logic.model

data class WeatherItem(
    val results: List<Result>
)

data class Result(
    val location: Location? = null,
    val now: Now? = null,
    val lastUpdate: String? = null
)

data class Location(
    val id: String? = null,
    val name: String? = null,
    val country: String? = null,
    val path: String? = null,
    val timezone: String? = null,
    val timezoneOffset: String? = null
)

data class Now(
    val text: String? = null,
    val code: String? = null,
    val temperature: String? = null
)