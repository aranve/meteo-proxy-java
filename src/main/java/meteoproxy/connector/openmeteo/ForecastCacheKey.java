package meteoproxy.connector.openmeteo;

import java.math.BigDecimal;

public record ForecastCacheKey(BigDecimal latitude, BigDecimal longitude) {
}
