package meteoproxy.domain.meteo;

import java.math.BigDecimal;

public record Weather(
        Location location,
        CurrentWeather current,
        String source,
        String retrievedAt
) {}

