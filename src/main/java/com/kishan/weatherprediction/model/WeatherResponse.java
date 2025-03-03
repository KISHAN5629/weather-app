package com.kishan.weatherprediction.model;

import java.util.List;

public class WeatherResponse {
    private String city;
    private List<Forecast> forecasts;

    public WeatherResponse(String city, List<Forecast> forecasts) {
        this.city = city;
        this.forecasts = forecasts;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<Forecast> forecasts) {
        this.forecasts = forecasts;
    }
}
