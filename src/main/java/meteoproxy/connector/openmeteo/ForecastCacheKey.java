package meteoproxy.connector.openmeteo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ForecastCacheKey(BigDecimal latitude, BigDecimal longitude) {

    public ForecastCacheKey {
        latitude = latitude.setScale(2, RoundingMode.HALF_UP);
        longitude = longitude.setScale(2, RoundingMode.HALF_UP);
    }
}
