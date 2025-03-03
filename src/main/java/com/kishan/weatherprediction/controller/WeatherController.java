package com.kishan.weatherprediction.controller;

import com.kishan.weatherprediction.service.WeatherService;
import com.kishan.weatherprediction.model.WeatherResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@Tag(name = "Weather Controller", description = "Endpoints for weather forecasts")
public class WeatherController {

    @Autowired
    private WeatherService weatherService; // Use Interface Instead of Implementation

    @GetMapping("/forecast")
    @Operation(summary = "Get weather forecast", description = "Fetches the next 3-day weather forecast for a given city")
    public ResponseEntity<WeatherResponse> getWeatherForecast(@RequestParam String city) {
        return ResponseEntity.ok(weatherService.getWeatherForecast(city));
    }
}
