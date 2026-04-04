package meteoproxy.connector.openmeteo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record GetForecastResponse(
        String timezone,
        CurrentDto current
) {}

