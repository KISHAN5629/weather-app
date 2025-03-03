package com.kishan.weatherprediction.service;

import com.kishan.weatherprediction.model.WeatherResponse;

public interface WeatherService {
    WeatherResponse getWeatherForecast(String city);
}
