package meteoproxy.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import meteoproxy.domain.meteo.model.Weather;

@Schema(description = "Weather information for a specific location")
public record WeatherDto(
        @Schema(description = "Geographic location coordinates", example = "{\"lat\": 52.52, \"lon\": 13.41}")
        LocationDto location,
        @Schema(description = "Current weather conditions", example = "{\"temperatureC\": 15.5, \"windSpeedKmh\": 12.3}")
        CurrentWeatherDto current,
        @Schema(description = "Data source identifier", example = "open-meteo")
        String source,
        @Schema(description = "Timestamp when the data was retrieved in Zulu time (ISO 8601 format)", example = "2026-04-07T12:00:00Z")
        String retrievedAt
) {
    public static WeatherDto from(Weather weather) {
        return new WeatherDto(
                LocationDto.from(weather.location()),
                CurrentWeatherDto.from(weather.current()),
                weather.source(),
                weather.retrievedAt()
        );
    }
}
