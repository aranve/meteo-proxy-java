package meteoproxy.domain.meteo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Location(BigDecimal lat, BigDecimal lon) {

    public Location {
        lat = lat.setScale(2, RoundingMode.HALF_UP);
        lon = lon.setScale(2, RoundingMode.HALF_UP);
    }
}

