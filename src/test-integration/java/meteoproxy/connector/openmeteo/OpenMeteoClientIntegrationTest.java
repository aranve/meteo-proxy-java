package meteoproxy.connector.openmeteo;

import meteoproxy.config.HttpConfig;
import meteoproxy.config.RetryConfig;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("integration-test")
@SpringBootTest(classes = {
        OpenMeteoConfig.class,
        HttpConfig.class,
        RetryConfig.class,
        LogbookAutoConfiguration.class
})
class OpenMeteoClientIntegrationTest {

    @Autowired
    @Qualifier("openMeteoClient")
    private WebClient sut;

    @Test
    void shouldFetchForecastFromOpenMeteo() {
        // given
        BigDecimal latitude = new BigDecimal("20.10");
        BigDecimal longitude = new BigDecimal("40.20");

        // when
        GetForecastResponse response = sut.get()
                .uri(b -> b.path("forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("current", "temperature_2m,wind_speed_10m")
                        .build())
                .retrieve()
                .bodyToMono(GetForecastResponse.class)
                .block();

        // then
        assertNotNull(response);
        assertNotNull(response.current());
    }
}


