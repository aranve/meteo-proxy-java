package meteoproxy.connector.openmeteo;

import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.domain.exception.ExternalApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

public class OpenMeteoConnector {

    private static final Logger LOG = LoggerFactory.getLogger(OpenMeteoConnector.class);
    private static final String DEFAULT_CURRENT_PARAMS = "temperature_2m,wind_speed_10m";

    private final WebClient webClient;

    public OpenMeteoConnector(WebClient webClient) {
        this.webClient = webClient;
    }

    @Cacheable("meteoServiceCache")
    public GetForecastResponse getCurrentForecast(BigDecimal latitude, BigDecimal longitude) {
        try {
            return webClient.get()
                    .uri(b -> b.path("forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", DEFAULT_CURRENT_PARAMS)
                            .build())
                    .retrieve()
                    .bodyToMono(GetForecastResponse.class)
                    .block();
        } catch (Exception e) {
            LOG.error("Request to open-meteo failed for latitude: {}, longitude: {}", latitude, longitude);
            throw new ExternalApiException("Could not get current forecast for latitude: " + latitude + ", longitude: " + longitude);
        }
    }
}
