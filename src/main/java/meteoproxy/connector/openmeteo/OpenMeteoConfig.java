package meteoproxy.connector.openmeteo;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.domain.meteo.model.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenMeteoConfig {

    @Bean
    public RestClient openMeteoClient(
            @Value("${external.open-meteo-api.url}") String url,
            ClientHttpRequestFactory clientHttpRequestFactory
    ) {
        return RestClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(clientHttpRequestFactory)
                .build();
    }

    @Bean
    public OpenMeteoConnector openMeteoConnector(
            RestClient openMeteoClient,
            Cache<Location, GetForecastResponse> forecastCache,
            RetryRegistry retryRegistry,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        return new OpenMeteoConnector(
                openMeteoClient,
                forecastCache,
                retryRegistry.retry("openMeteoApi"),
                circuitBreakerRegistry.circuitBreaker("openMeteoApi")
        );
    }
}
