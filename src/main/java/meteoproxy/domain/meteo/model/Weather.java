package meteoproxy.domain.meteo.model;

public record Weather(
        Location location,
        CurrentWeather current,
        String source,
        String retrievedAt
) {}

