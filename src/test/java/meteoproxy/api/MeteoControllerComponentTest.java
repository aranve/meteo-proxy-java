package meteoproxy.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import meteoproxy.MeteoProxyApp;
import meteoproxy.connector.openmeteo.dto.CurrentDto;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.connector.openmeteo.OpenMeteoClient;
import meteoproxy.domain.meteo.model.CurrentWeather;
import meteoproxy.domain.meteo.model.Location;
import meteoproxy.domain.meteo.model.Weather;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import retrofit2.mock.Calls;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {MeteoProxyApp.class, MeteoControllerComponentTestConfig.class}
)
class MeteoControllerComponentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private OpenMeteoClient openMeteoClient;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void resetMocks() {
        Mockito.reset(openMeteoClient);
    }

    @Test
    void getWeatherShouldReturnWeatherDataForValidCoordinates() throws IOException {
        // given
        BigDecimal latitude = new BigDecimal("20.10");
        BigDecimal longitude = new BigDecimal("40.20");

        GetForecastResponse forecastResponse = new GetForecastResponse(
                "GMT",
                new CurrentDto("2026-01-23T13:15:00", new BigDecimal("2.3"), new BigDecimal("12.3"))
        );
        when(openMeteoClient.getCurrentForecast(eq(latitude), eq(longitude), anyString()))
                .thenReturn(Calls.response(forecastResponse));

        // when
        Response response = client.newCall(prepareRequest(latitude, longitude)).execute();

        // then
        Weather expectedResponse = new Weather(
                new Location(new BigDecimal("20.10"), new BigDecimal("40.20")),
                new CurrentWeather(new BigDecimal("2.3"), new BigDecimal("12.3")),
                "open-meteo",
                "2026-01-23T13:15:00Z"
        );
        String json = response.body().string();
        Weather actualResponse = objectMapper.readValue(json, Weather.class);

        assertEquals(200, response.code());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getWeatherShouldReturnInternalServerErrorWhenCallToExternalApiFails() throws IOException {
        // given
        BigDecimal latitude = new BigDecimal("9.11");
        BigDecimal longitude = new BigDecimal("3.20");

        RuntimeException exception = new RuntimeException("Call failed");
        when(openMeteoClient.getCurrentForecast(eq(latitude), eq(longitude), anyString()))
                .thenReturn(Calls.failure(exception));

        // when
        Response response = client.newCall(prepareRequest(latitude, longitude)).execute();

        // then
        assertEquals(500, response.code());
    }

    @Test
    void getWeatherShouldReturnBadRequestForInvalidParameters() throws IOException {
        // given
        BigDecimal invalidLatitude = new BigDecimal("200");
        BigDecimal longitude = new BigDecimal("200");

        // when
        Response response = client.newCall(prepareRequest(invalidLatitude, longitude)).execute();

        // then
        assertEquals(400, response.code());
    }

    @Test
    void getWeatherShouldReturnUnauthorizedForUnauthenticatedUser() throws IOException {
        // given
        BigDecimal latitude = new BigDecimal("21.37");
        BigDecimal longitude = new BigDecimal("4.20");

        String url = "http://localhost:" + port + "/v1/meteo/weather?lat=" + latitude + "&lon=" + longitude;
        Request request = new Request.Builder().url(url).build();

        // when
        Response response = client.newCall(request).execute();

        // then
        assertEquals(401, response.code());
    }

    private Request prepareRequest(BigDecimal lat, BigDecimal lon) {
        String url = "http://localhost:" + port + "/v1/meteo/weather?lat=" + lat + "&lon=" + lon;
        return new Request.Builder()
                .url(url)
                .header(HttpHeaders.AUTHORIZATION, Credentials.basic("user", "password"))
                .build();
    }
}

