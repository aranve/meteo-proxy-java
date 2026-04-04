package meteoproxy.domain.meteo;

import meteoproxy.connector.openmeteo.CurrentDto;
import meteoproxy.connector.openmeteo.GetForecastResponse;
import meteoproxy.connector.openmeteo.OpenMeteoConnector;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MeteoServiceTest {

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
                .thenReturn(response);

        BigDecimal lat = new BigDecimal("52.5249");
        BigDecimal lon = new BigDecimal("13.4051");

        // when
        Weather result = sut.getCurrentWeather(lat, lon);

        // then
        Weather expectedResult = new Weather(
                new Location(lat, lon),
                new CurrentWeather(new BigDecimal("12.3"), new BigDecimal("5.4")),
                "open-meteo",
                "2026-01-21T12:00:00Z"
        );
        assertEquals(expectedResult, result);
    }
}

