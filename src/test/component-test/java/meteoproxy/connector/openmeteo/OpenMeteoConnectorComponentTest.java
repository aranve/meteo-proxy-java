package meteoproxy.connector.openmeteo;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import meteoproxy.config.CacheConfig;
import meteoproxy.config.HttpConfig;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.domain.exception.ExternalApiException;
import meteoproxy.domain.meteo.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ActiveProfiles("component-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({HttpConfig.class, CacheConfig.class})
class OpenMeteoConnectorComponentTest {

    private static final Location LOCATION = new Location(new BigDecimal("52.52"), new BigDecimal("13.41"));
    private static final String FORECAST_JSON = "{\"timezone\":\"Europe/Berlin\",\"current\":{\"time\":\"2026-01-23T13:15\",\"temperature\":10.5,\"windSpeed\":5.2}}";

    @Autowired
    private Cache<Location, GetForecastResponse> forecastCache;

    @Autowired
    private ClientHttpRequestFactory clientHttpRequestFactory;

    @Autowired
    private RetryRegistry retryRegistry;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private MockRestServiceServer mockServer;
    private OpenMeteoConnector sut;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("http://test-api.com")
                .requestFactory(clientHttpRequestFactory);

        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();

        forecastCache.invalidateAll();
        circuitBreakerRegistry.circuitBreaker("openMeteoApi").reset();

        sut = new OpenMeteoConnector(
                restClient,
                forecastCache,
                retryRegistry.retry("openMeteoApi"),
                circuitBreakerRegistry.circuitBreaker("openMeteoApi")
        );
    }

    @Test
    void shouldReturnForecast() {
        // given
        mockServer.expect(requestTo("http://test-api.com/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m"))
                .andRespond(withSuccess(FORECAST_JSON, MediaType.APPLICATION_JSON));

        // when
        GetForecastResponse result = sut.getCurrentForecast(LOCATION);

        // then
        assertNotNull(result);
        assertEquals("Europe/Berlin", result.timezone());
        assertNotNull(result.current());
        mockServer.verify();
    }

    @Test
    void shouldRetryTwiceAndSucceedOnThirdAttempt() {
        // given
        mockServer.expect(requestTo("http://test-api.com/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m"))
                .andRespond(withServerError());
        mockServer.expect(requestTo("http://test-api.com/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m"))
                .andRespond(withServerError());
        mockServer.expect(requestTo("http://test-api.com/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m"))
                .andRespond(withSuccess(FORECAST_JSON, MediaType.APPLICATION_JSON));

        // when
        GetForecastResponse result = sut.getCurrentForecast(LOCATION);

        // then
        assertNotNull(result);
        assertEquals("Europe/Berlin", result.timezone());
        mockServer.verify();
    }

    @Test
    void shouldThrowExternalApiExceptionAfterRetryIsExhausted() {
        // given
        mockServer.expect(requestTo("http://test-api.com/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m"))
                .andRespond(withServerError());
        mockServer.expect(requestTo("http://test-api.com/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m"))
                .andRespond(withServerError());
        mockServer.expect(requestTo("http://test-api.com/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m"))
                .andRespond(withServerError());

        // when + then
        assertThrows(ExternalApiException.class, () -> sut.getCurrentForecast(LOCATION));
        mockServer.verify();
    }

    @Test
    void shouldCacheResultsAndNotCallApiOnSecondRequest() {
        // given
        mockServer.expect(requestTo("http://test-api.com/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m"))
                .andRespond(withSuccess(FORECAST_JSON, MediaType.APPLICATION_JSON));

        // when
        GetForecastResponse result1 = sut.getCurrentForecast(LOCATION);
        GetForecastResponse result2 = sut.getCurrentForecast(LOCATION);

        // then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("Europe/Berlin", result1.timezone());
        assertEquals("Europe/Berlin", result2.timezone());
        mockServer.verify();
    }

    @Test
    void shouldNotRetryOnClientErrors() {
        // given
        mockServer.expect(requestTo("http://test-api.com/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // when + then
        assertThrows(Exception.class, () -> sut.getCurrentForecast(LOCATION));
        mockServer.verify();
    }
}
