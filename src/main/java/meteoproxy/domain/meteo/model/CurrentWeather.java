package meteoproxy.domain.meteo.model;

import java.math.BigDecimal;

public record CurrentWeather(BigDecimal temperatureC, BigDecimal windSpeedKmh) {}

