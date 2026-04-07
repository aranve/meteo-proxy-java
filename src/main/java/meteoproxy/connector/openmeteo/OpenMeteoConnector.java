package meteoproxy.connector.openmeteo;

import com.github.benmanes.caffeine.cache.Cache;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.domain.exception.ExternalApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class OpenMeteoConnector {

    private static final Logger LOG = LoggerFactory.getLogger(OpenMeteoConnector.class);
    private static final String DEFAULT_CURRENT_PARAMS = "temperature_2m,wind_speed_10m";

    private final WebClient webClient;
    private final Cache<ForecastCacheKey, Mono<GetForecastResponse>> cache;

    public OpenMeteoConnector(WebClient webClient, Cache<ForecastCacheKey, Mono<GetForecastResponse>> cache) {
        this.webClient = webClient;
        this.cache = cache;
    }

    public Mono<GetForecastResponse> getCurrentForecast(BigDecimal latitude, BigDecimal longitude) {
        var cacheKey = new ForecastCacheKey(latitude, longitude);
        return cache.asMap().computeIfAbsent(cacheKey, k -> fetchFromApi(k.latitude(), k.longitude()).cache());
    }

    private Mono<GetForecastResponse> fetchFromApi(BigDecimal latitude, BigDecimal longitude) {
        return webClient.get()
                .uri(b -> b.path("forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("current", DEFAULT_CURRENT_PARAMS)
                        .build())
                .retrieve()
                .bodyToMono(GetForecastResponse.class)
                .onErrorMap(e -> {
                    LOG.error("Request to open-meteo failed for latitude: {}, longitude: {}", latitude, longitude, e);
                    return new ExternalApiException("Could not get current forecast for latitude: " + latitude + ", longitude: " + longitude);
                });
    }
}
