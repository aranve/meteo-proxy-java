package meteoproxy.connector.openmeteo;

import meteoproxy.config.CacheConfig;
import meteoproxy.config.HttpConfig;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({HttpConfig.class, CacheConfig.class, OpenMeteoConfig.class})
class OpenMeteoClientIntegrationTest {

    @Autowired
    @Qualifier("openMeteoClient")
    private RestClient sut;

    @Test
    void shouldFetchForecastFromOpenMeteo() {
        // given
        BigDecimal latitude = new BigDecimal("20.10");
        BigDecimal longitude = new BigDecimal("40.20");

        // when
        GetForecastResponse response = sut.get()
                .uri(uriBuilder -> uriBuilder
                        .path("forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("current", "temperature_2m,wind_speed_10m")
                        .build())
                .retrieve()
                .body(GetForecastResponse.class);

        // then
        assertNotNull(response);
        assertNotNull(response.current());
    }
}


