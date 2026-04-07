package meteoproxy.api;

import meteoproxy.MeteoProxyApp;
import meteoproxy.connector.openmeteo.OpenMeteoConnector;
import meteoproxy.connector.openmeteo.dto.CurrentDto;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.domain.meteo.model.CurrentWeather;
import meteoproxy.domain.meteo.model.Location;
import meteoproxy.domain.meteo.model.Weather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ActiveProfiles("component-test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MeteoProxyApp.class
)
class MeteoControllerComponentTest {

    @Autowired
    private ApplicationContext applicationContext;

    private WebTestClient webTestClient;

    @MockitoBean
    private OpenMeteoConnector openMeteoConnector;


    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
        Mockito.reset(openMeteoConnector);
    }

    @Test
    void getWeatherShouldReturnWeatherDataForValidCoordinates() {
        // given
        BigDecimal latitude = new BigDecimal("20.10");
        BigDecimal longitude = new BigDecimal("40.20");

        GetForecastResponse forecastResponse = new GetForecastResponse(
                "GMT",
                new CurrentDto("2026-01-23T13:15:00", new BigDecimal("2.3"), new BigDecimal("12.3"))
        );
        when(openMeteoConnector.getCurrentForecast(eq(latitude), eq(longitude)))
                .thenReturn(Mono.just(forecastResponse));

        // when + then
        Weather expectedResponse = new Weather(
                new Location(new BigDecimal("20.10"), new BigDecimal("40.20")),
                new CurrentWeather(new BigDecimal("2.3"), new BigDecimal("12.3")),
                "open-meteo",
                "2026-01-23T13:15:00Z"
        );

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/meteo/weather")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Weather.class)
                .isEqualTo(expectedResponse);
    }

    @Test
    void getWeatherShouldReturnInternalServerErrorWhenCallToExternalApiFails() {
        // given
        BigDecimal latitude = new BigDecimal("9.11");
        BigDecimal longitude = new BigDecimal("3.20");

        when(openMeteoConnector.getCurrentForecast(eq(latitude), eq(longitude)))
                .thenReturn(Mono.error(new RuntimeException("Call failed")));

        // when + then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/meteo/weather")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getWeatherShouldReturnBadRequestForInvalidParameters() {
        // given
        BigDecimal latitude = new BigDecimal("200");
        BigDecimal longitude = new BigDecimal("200");

        // when + then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/meteo/weather")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getWeatherShouldReturnUnauthorizedForUnauthenticatedUser() {
        // when + then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/meteo/weather")
                        .queryParam("lat", "21.37")
                        .queryParam("lon", "4.20")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}

