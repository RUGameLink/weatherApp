package com.example.weatherapp

data class WeatherData(
    var feelsLikeText: String ?= null,
    var windDirText: String ?= null,
    var windText: String ?= null,
    var pressureText: String ?= null,
    var humidityText: String ?= null,
    var eventText: String ?= null
)
