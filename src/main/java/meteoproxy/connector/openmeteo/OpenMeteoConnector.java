package meteoproxy.connector.openmeteo;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.domain.exception.ExternalApiException;
import meteoproxy.domain.meteo.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;


public class OpenMeteoConnector {

    private static final Logger LOG = LoggerFactory.getLogger(OpenMeteoConnector.class);
    private static final String DEFAULT_CURRENT_PARAMS = "temperature_2m,wind_speed_10m";

    private final RestClient restClient;
    private final Cache<Location, GetForecastResponse> cache;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public OpenMeteoConnector(
            RestClient restClient,
            Cache<Location, GetForecastResponse> cache,
            Retry retry,
            CircuitBreaker circuitBreaker
    ) {
        this.restClient = restClient;
        this.cache = cache;
        this.retry = retry;
        this.circuitBreaker = circuitBreaker;
    }

    public GetForecastResponse getCurrentForecast(Location location) {
        return cache.get(location, this::getCurrentForecastInternal);
    }

    private GetForecastResponse getCurrentForecastInternal(Location location) {
        var supplier = CircuitBreaker.decorateSupplier(circuitBreaker,
                () -> fetchFromApi(location));
        var decoratedSupplier = Retry.decorateSupplier(retry, supplier);
        return decoratedSupplier.get();
    }

    private GetForecastResponse fetchFromApi(Location location) {
        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("forecast")
                            .queryParam("latitude", location.lat())
                            .queryParam("longitude", location.lon())
                            .queryParam("current", DEFAULT_CURRENT_PARAMS)
                            .build())
                    .retrieve()
                    .body(GetForecastResponse.class);

            if (response == null) {
                LOG.error("Received null response from open-meteo API for location: {}", location);
                throw new ExternalApiException("Received null response from external API");
            }

            return response;
        } catch (HttpClientErrorException e) {
            LOG.error("Client error from open-meteo API for location: {}", location);
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling open-meteo API for location: {}", location, e);
            throw new ExternalApiException("Error contacting weather API");
        }
    }
}
