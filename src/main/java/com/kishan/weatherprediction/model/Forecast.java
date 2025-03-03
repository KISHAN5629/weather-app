package com.kishan.weatherprediction.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Forecast {
    @JsonProperty("date")
    private String date;

    @JsonProperty("tempMax")
    private double tempMax;

    @JsonProperty("tempMin")
    private double tempMin;

    @JsonProperty("condition")
    private String condition;

    @JsonProperty("windSpeed")
    private double windSpeed;

    @JsonProperty("recommendation")
    private String recommendation;

}
