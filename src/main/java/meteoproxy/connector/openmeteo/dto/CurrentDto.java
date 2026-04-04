package meteoproxy.connector.openmeteo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record CurrentDto(
        String time,
        @JsonProperty("temperature_2m") BigDecimal temperature,
        @JsonProperty("wind_speed_10m") BigDecimal windSpeed
) {}

