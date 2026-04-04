package meteoproxy.api;

import meteoproxy.connector.openmeteo.OpenMeteoClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MeteoControllerComponentTestConfig {

    @Bean
    @Primary
    public OpenMeteoClient openMeteoClient() {
        return Mockito.mock(OpenMeteoClient.class);
    }
}

