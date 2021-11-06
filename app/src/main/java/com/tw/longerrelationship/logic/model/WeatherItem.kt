package com.tw.longerrelationship.logic.model

data class WeatherItem (
    val results: List<Result>
)

data class Result (
    val location: Location,
    val now: Now,
    val lastUpdate: String
)

data class Location (
    val id: String,
    val name: String,
    val country: String,
    val path: String,
    val timezone: String,
    val timezoneOffset: String
)

data class Now (
    val text: String,
    val code: String,
    val temperature: String
)