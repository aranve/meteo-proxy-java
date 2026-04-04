package meteoproxy.connector.openmeteo.dto;

public record GetForecastResponse(
        String timezone,
        CurrentDto current
) {}

