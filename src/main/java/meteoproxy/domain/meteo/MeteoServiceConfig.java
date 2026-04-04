package meteoproxy.domain.meteo;

import meteoproxy.connector.openmeteo.OpenMeteoConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeteoServiceConfig {

    @Bean
    public MeteoService meteoService(OpenMeteoConnector openMeteoConnector) {
        return new MeteoService(openMeteoConnector);
    }
}

