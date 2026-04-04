package meteoproxy.connector.openmeteo;

import meteoproxy.config.HttpConfig;
import meteoproxy.config.JacksonConfig;
import meteoproxy.config.RetryConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        OpenMeteoConfig.class,
        HttpConfig.class,
        JacksonConfig.class,
        LogbookAutoConfiguration.class,
        RetryConfiguration.class
})
class OpenMeteoClientIntegrationTest {

    @Autowired
    private OpenMeteoClient sut;

    @Test
    void shouldFetchResponseFromOpenMeteo() throws IOException {
        // given
        BigDecimal latitude = new BigDecimal("20.10");
        BigDecimal longitude = new BigDecimal("40.20");

        // when
        Response<GetForecastResponse> response = sut
                .getCurrentForecast(latitude, longitude, "temperature_2m,wind_speed_10m")
                .execute();

        // then
        assertEquals(200, response.code());
        assertNotNull(response.body());
    }
}

