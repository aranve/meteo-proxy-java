package meteoproxy.domain.meteo;

import java.math.BigDecimal;

public record CurrentWeather(BigDecimal temperatureC, BigDecimal windSpeedKmh) {}

