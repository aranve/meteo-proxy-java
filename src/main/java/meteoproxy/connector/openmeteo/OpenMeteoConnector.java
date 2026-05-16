package meteoproxy.connector.openmeteo;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.domain.exception.ExternalApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

public class OpenMeteoConnector {

    private static final Logger LOG = LoggerFactory.getLogger(OpenMeteoConnector.class);
    private static final String DEFAULT_CURRENT_PARAMS = "temperature_2m,wind_speed_10m";

    private final RestClient restClient;
    private final Cache<ForecastCacheKey, GetForecastResponse> cache;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public OpenMeteoConnector(
            RestClient restClient,
            Cache<ForecastCacheKey, GetForecastResponse> cache,
            Retry retry,
            CircuitBreaker circuitBreaker
    ) {
        this.restClient = restClient;
        this.cache = cache;
        this.retry = retry;
        this.circuitBreaker = circuitBreaker;
    }

    public GetForecastResponse getCurrentForecast(BigDecimal latitude, BigDecimal longitude) {
        var cacheKey = new ForecastCacheKey(latitude, longitude);
        return cache.get(cacheKey, k -> getCurrentForecastInternal(k.latitude(), k.longitude()));
    }

    private GetForecastResponse getCurrentForecastInternal(BigDecimal latitude, BigDecimal longitude) {
        var supplier = CircuitBreaker.decorateSupplier(circuitBreaker,
                () -> fetchFromApi(latitude, longitude));
        var decoratedSupplier = Retry.decorateSupplier(retry, supplier);
        return decoratedSupplier.get();
    }

    private GetForecastResponse fetchFromApi(BigDecimal latitude, BigDecimal longitude) {
        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", DEFAULT_CURRENT_PARAMS)
                            .build())
                    .retrieve()
                    .body(GetForecastResponse.class);

            if (response == null) {
                LOG.error("Received null response from open-meteo API for latitude: {}, longitude: {}", latitude, longitude);
                throw new ExternalApiException("Received null response from external API");
            }

            return response;
        } catch (HttpClientErrorException e) {
            LOG.error("Client error from open-meteo API: status={}, body={}, latitude={}, longitude={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), latitude, longitude);
            throw e;
        } catch (Exception e) {
            LOG.error("Error calling open-meteo API for latitude: {}, longitude: {}", latitude, longitude, e);
            throw new ExternalApiException("Error contacting weather API");
        }
    }
}
