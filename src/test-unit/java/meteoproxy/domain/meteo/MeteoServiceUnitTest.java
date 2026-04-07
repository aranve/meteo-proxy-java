package meteoproxy.domain.meteo;

import meteoproxy.connector.openmeteo.OpenMeteoConnector;
import meteoproxy.connector.openmeteo.dto.CurrentDto;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import meteoproxy.domain.meteo.model.CurrentWeather;
import meteoproxy.domain.meteo.model.Location;
import meteoproxy.domain.meteo.model.Weather;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MeteoServiceUnitTest {

    private final OpenMeteoConnector openMeteoConnector = mock(OpenMeteoConnector.class);
    private final MeteoService sut = new MeteoService(openMeteoConnector);

    @Test
    void shouldReturnResultWithRoundedCoordinates() {
        // given
        GetForecastResponse response = new GetForecastResponse(
                "UTC",
                new CurrentDto("2026-01-21T12:00", new BigDecimal("12.3"), new BigDecimal("5.4"))
        );
        when(openMeteoConnector.getCurrentForecast(new BigDecimal("52.52"), new BigDecimal("13.41")))
                .thenReturn(Mono.just(response));

        BigDecimal lat = new BigDecimal("52.5249");
        BigDecimal lon = new BigDecimal("13.4051");

        Weather expectedResult = new Weather(
                new Location(lat, lon),
                new CurrentWeather(new BigDecimal("12.3"), new BigDecimal("5.4")),
                "open-meteo",
                "2026-01-21T12:00:00Z"
        );

        // when + then
        StepVerifier.create(sut.getCurrentWeather(lat, lon))
                .expectNext(expectedResult)
                .verifyComplete();
    }
}

