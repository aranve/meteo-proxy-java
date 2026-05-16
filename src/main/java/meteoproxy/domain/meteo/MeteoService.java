package meteoproxy.domain.meteo;

import meteoproxy.connector.openmeteo.OpenMeteoConnector;
import meteoproxy.domain.meteo.model.CurrentWeather;
import meteoproxy.domain.meteo.model.Location;
import meteoproxy.domain.meteo.model.Weather;
import meteoproxy.domain.utils.DateTimeUtils;

public class MeteoService {

    private final OpenMeteoConnector openMeteoConnector;

    public MeteoService(OpenMeteoConnector openMeteoConnector) {
        this.openMeteoConnector = openMeteoConnector;
    }

    public Weather getCurrentWeather(Location location) {
        var response = openMeteoConnector.getCurrentForecast(location);

        return new Weather(
                location,
                new CurrentWeather(response.current().temperature(), response.current().windSpeed()),
                "open-meteo",
                DateTimeUtils.convertToZuluDateTime(response.current().time(), response.timezone())
        );
    }
}

