package meteoproxy.connector.openmeteo;

import meteoproxy.config.HttpConfig;
import meteoproxy.config.RetryConfig;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.domain.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("component-test")
@SpringBootTest(classes = {
        OpenMeteoConfig.class,
        HttpConfig.class,
        RetryConfig.class
})
class OpenMeteoConnectorComponentTest {

    private static final BigDecimal LATITUDE = new BigDecimal("52.52");
    private static final BigDecimal LONGITUDE = new BigDecimal("13.41");
    private static final String FORECAST_JSON = "{\"timezone\":\"Europe/Berlin\",\"current\":null}";

    @Autowired
    private ExchangeFilterFunction retryFilter;

    private ExchangeFunction exchangeFunction;
    private OpenMeteoConnector sut;

    @BeforeEach
    void setUp() {
        exchangeFunction = mock(ExchangeFunction.class);
        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .filter(retryFilter)
                .build();
        sut = new OpenMeteoConnector(webClient);
    }

    @Test
    void shouldReturnForecast() {
        // given
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(okResponse()));

        // when
        GetForecastResponse result = sut.getCurrentForecast(LATITUDE, LONGITUDE);

        // then
        assertEquals("Europe/Berlin", result.timezone());
        verify(exchangeFunction, times(1)).exchange(any());
    }

    @Test
    void shouldRetryOnceAndSucceedOnSecondAttempt() {
        // given
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(serverErrorResponse()))
                .thenReturn(Mono.just(okResponse()));

        // when
        GetForecastResponse result = sut.getCurrentForecast(LATITUDE, LONGITUDE);

        // then
        assertEquals("Europe/Berlin", result.timezone());
        verify(exchangeFunction, times(2)).exchange(any());
    }

    @Test
    void shouldThrowExternalApiExceptionAfterRetryIsExhausted() {
        // given
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(serverErrorResponse()))
                .thenReturn(Mono.just(serverErrorResponse()))
                .thenReturn(Mono.just(serverErrorResponse()));

        // when + then
        assertThrows(ExternalApiException.class, () -> sut.getCurrentForecast(LATITUDE, LONGITUDE));
        verify(exchangeFunction, times(3)).exchange(any());
    }

    private static ClientResponse okResponse() {
        var buffer = DefaultDataBufferFactory.sharedInstance
                .wrap(OpenMeteoConnectorComponentTest.FORECAST_JSON.getBytes(StandardCharsets.UTF_8));
        return ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Flux.just(buffer))
                .build();
    }

    private static ClientResponse serverErrorResponse() {
        return ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

