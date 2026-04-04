package meteoproxy.connector.openmeteo;

import io.github.resilience4j.retry.Retry;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.domain.exception.ExternalApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import retrofit2.Response;

import java.math.BigDecimal;

public class OpenMeteoConnector {

    private static final Logger LOG = LoggerFactory.getLogger(OpenMeteoConnector.class);
    private static final String DEFAULT_CURRENT_PARAMS = "temperature_2m,wind_speed_10m";

    private final OpenMeteoClient openMeteoClient;
    private final Retry retry;

    public OpenMeteoConnector(OpenMeteoClient openMeteoClient, Retry retry) {
        this.openMeteoClient = openMeteoClient;
        this.retry = retry;
    }

    @Cacheable(cacheNames = "meteoServiceCache")
    public GetForecastResponse getCurrentForecast(BigDecimal latitude, BigDecimal longitude) {

        Response<GetForecastResponse> response;
        try {
            response = retry.executeCallable(() ->
                    openMeteoClient.getCurrentForecast(latitude, longitude, DEFAULT_CURRENT_PARAMS).execute());
        } catch (Exception e) {
            LOG.error("Request to open-meteo to get current forecast failed for latitude: {}, longitude: {}", latitude, longitude);
            throw new ExternalApiException("Could not get current forecast for latitude: " + latitude + ", longitude: " + longitude);
        }

        if (!response.isSuccessful()) {
            LOG.error("Request to open-meteo to get current forecast failed for latitude: {}, longitude: {}", latitude, longitude);
            throw new ExternalApiException("Could not get current forecast for latitude: " + latitude + ", longitude: " + longitude);
        }

        return response.body();
    }
}

