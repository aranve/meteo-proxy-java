package meteoproxy.api;

import meteoproxy.MeteoProxyApp;
import meteoproxy.connector.openmeteo.OpenMeteoConnector;
import meteoproxy.connector.openmeteo.dto.CurrentDto;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.domain.exception.ExternalApiException;
import meteoproxy.domain.meteo.model.CurrentWeather;
import meteoproxy.domain.meteo.model.Location;
import meteoproxy.domain.meteo.model.Weather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ActiveProfiles("component-test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MeteoProxyApp.class
)
class MeteoControllerComponentTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private OpenMeteoConnector openMeteoConnector;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void resetMocks() {
        Mockito.reset(openMeteoConnector);
    }

    @Test
    void getWeatherShouldReturnWeatherDataForValidCoordinates() throws Exception {
        // given
        BigDecimal latitude = new BigDecimal("20.10");
        BigDecimal longitude = new BigDecimal("40.20");
        Location location = new Location(latitude, longitude);

        GetForecastResponse forecastResponse = new GetForecastResponse(
                "GMT",
                new CurrentDto("2026-01-23T13:15:00", new BigDecimal("2.3"), new BigDecimal("12.3"))
        );
        when(openMeteoConnector.getCurrentForecast(eq(location)))
                .thenReturn(forecastResponse);

        // when
        HttpResponse<String> response = client.send(
                prepareRequest(latitude, longitude),
                HttpResponse.BodyHandlers.ofString()
        );

        // then
        Weather expectedResponse = new Weather(
                new Location(new BigDecimal("20.10"), new BigDecimal("40.20")),
                new CurrentWeather(new BigDecimal("2.3"), new BigDecimal("12.3")),
                "open-meteo",
                "2026-01-23T13:15:00Z"
        );
        Weather actualResponse = objectMapper.readValue(response.body(), Weather.class);

        assertEquals(200, response.statusCode());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getWeatherShouldReturnInternalServerErrorWhenCallToExternalApiFails() throws Exception {
        // given
        BigDecimal latitude = new BigDecimal("9.11");
        BigDecimal longitude = new BigDecimal("3.20");
        Location location = new Location(latitude, longitude);

        when(openMeteoConnector.getCurrentForecast(eq(location)))
                .thenThrow(new ExternalApiException("Call failed"));

        // when
        HttpResponse<String> response = client.send(
                prepareRequest(latitude, longitude),
                HttpResponse.BodyHandlers.ofString()
        );

        // then
        assertEquals(500, response.statusCode());
    }

    @Test
    void getWeatherShouldReturnBadRequestForInvalidParameters() throws Exception {
        // given
        BigDecimal invalidLatitude = new BigDecimal("200");
        BigDecimal longitude = new BigDecimal("200");

        // when
        HttpResponse<String> response = client.send(
                prepareRequest(invalidLatitude, longitude),
                HttpResponse.BodyHandlers.ofString()
        );

        // then
        assertEquals(400, response.statusCode());
    }

    @Test
    void getWeatherShouldReturnUnauthorizedForUnauthenticatedUser() throws Exception {
        // given
        String url = "http://localhost:" + port + "/v1/meteo/weather?lat=21.37&lon=4.20";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(401, response.statusCode());
    }

    private HttpRequest prepareRequest(BigDecimal lat, BigDecimal lon) {
        String url = "http://localhost:" + port + "/v1/meteo/weather?lat=" + lat + "&lon=" + lon;
        String credentials = Base64.getEncoder().encodeToString("user:password".getBytes());
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .GET()
                .build();
    }
}

