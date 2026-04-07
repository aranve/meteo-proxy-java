package meteoproxy.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import meteoproxy.domain.meteo.model.CurrentWeather;

import java.math.BigDecimal;

@Schema(description = "Current weather conditions")
public record CurrentWeatherDto(
        @Schema(description = "Temperature in degrees Celsius", example = "15.5")
        BigDecimal temperatureC,
        @Schema(description = "Wind speed in kilometers per hour", example = "12.3")
        BigDecimal windSpeedKmh
) {
    public static CurrentWeatherDto from(CurrentWeather currentWeather) {
        return new CurrentWeatherDto(currentWeather.temperatureC(), currentWeather.windSpeedKmh());
    }
}
