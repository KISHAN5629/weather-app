package com.kishan.weatherprediction.service;

import com.kishan.weatherprediction.config.WeatherConfig;
import com.kishan.weatherprediction.exception.WeatherException;
import com.kishan.weatherprediction.model.Forecast;
import com.kishan.weatherprediction.model.WeatherResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.key}")
    private String apiKey;


    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate;
    private final WeatherConfig weatherConfig;

    public WeatherServiceImpl(WeatherConfig weatherConfig) {
        this.restTemplate = new RestTemplate();
        this.weatherConfig = weatherConfig;
    }

    @Override
    @CircuitBreaker(name = "weatherService", fallbackMethod = "fallbackWeatherForecast")
    @Retry(name = "weatherService")
    public WeatherResponse getWeatherForecast(String city) {
        try {
            String url = String.format(weatherConfig.getApiUrl(), city, weatherConfig.getApiKey());
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return parseWeatherResponse(city, response.getBody());
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new WeatherException("City '" + city + "' not found. Please check the city name.");
            } else {
                throw new WeatherException("Failed to fetch weather data. Error: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new WeatherException("Error fetching weather data: " + e.getMessage());
        }
    }

    private WeatherResponse parseWeatherResponse(String city, String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode forecastList = rootNode.path("list");

            Map<String, Forecast> dailyForecasts = new LinkedHashMap<>(); // Maintain order

            for (JsonNode forecastNode : forecastList) {
                String date = forecastNode.path("dt_txt").asText().split(" ")[0]; // Extract only the date
                double tempMax = forecastNode.path("main").path("temp_max").asDouble();
                double tempMin = forecastNode.path("main").path("temp_min").asDouble();
                String condition = forecastNode.path("weather").get(0).path("main").asText();
                double windSpeed = forecastNode.path("wind").path("speed").asDouble();

                // Check if the date exists in the map, otherwise initialize it
                dailyForecasts.putIfAbsent(date, new Forecast(date, tempMax, tempMin, condition, windSpeed, ""));
                Forecast existingForecast = dailyForecasts.get(date);

                // Update max/min temperature
                existingForecast.setTempMax(Math.max(existingForecast.getTempMax(), tempMax));
                existingForecast.setTempMin(Math.min(existingForecast.getTempMin(), tempMin));

                // Update recommendation
                existingForecast.setRecommendation(getRecommendation(existingForecast.getTempMax(), condition, windSpeed));
            }

            // Limit to 3 days
            List<Forecast> summarizedForecasts = new ArrayList<>(dailyForecasts.values());

            if(summarizedForecasts.size()>3)
                summarizedForecasts = summarizedForecasts.subList(0, 3);
            else log.warn("Api got less than 3 days of weather data city :{} days :{}",city,summarizedForecasts.size());


            return new WeatherResponse(city, summarizedForecasts);
        } catch (Exception e) {
            throw new WeatherException("Error parsing weather data: " + e.getMessage());
        }
    }


    private String getRecommendation(double tempMax, String condition, double windSpeed) {
        if (condition.equalsIgnoreCase("Rain")) {
            return "Carry an umbrella!";
        } else if (tempMax > 40) {
            return "Use sunscreen lotion!";
        } else if (windSpeed > 20) {
            return "It’s too windy, watch out!";
        } else if (condition.equalsIgnoreCase("Thunderstorm")) {
            return "Don’t step out! A Storm is brewing!";
        }
        return "No special recommendation.";
    }

    public WeatherResponse fallbackWeatherForecast(String city, Throwable t) {
        return new WeatherResponse(city, new ArrayList<>());
    }
}
