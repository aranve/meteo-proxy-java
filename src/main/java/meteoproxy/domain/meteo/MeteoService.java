package meteoproxy.domain.meteo;

import meteoproxy.connector.openmeteo.OpenMeteoConnector;
import meteoproxy.domain.meteo.model.CurrentWeather;
import meteoproxy.domain.meteo.model.Location;
import meteoproxy.domain.meteo.model.Weather;
import meteoproxy.domain.utils.DateTimeUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MeteoService {

    private final OpenMeteoConnector openMeteoConnector;

    public MeteoService(OpenMeteoConnector openMeteoConnector) {
        this.openMeteoConnector = openMeteoConnector;
    }

    public Mono<Weather> getCurrentWeather(BigDecimal latitude, BigDecimal longitude) {
        BigDecimal roundedLat = latitude.setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedLon = longitude.setScale(2, RoundingMode.HALF_UP);

        return openMeteoConnector.getCurrentForecast(roundedLat, roundedLon)
                .map(response -> new Weather(
                        new Location(latitude, longitude),
                        new CurrentWeather(response.current().temperature(), response.current().windSpeed()),
                        "open-meteo",
                        DateTimeUtils.convertToZuluDateTime(response.current().time(), response.timezone())
                ));
    }
}

