package meteoproxy.connector.openmeteo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class OpenMeteoConfig {

    @Bean
    public WebClient openMeteoClient(
            @Value("${external.open-meteo-api.url}") String url,
            HttpClient httpClient,
            ExchangeFilterFunction retryFilter
    ) {
        return WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(retryFilter)
                .build();
    }

    @Bean
    public OpenMeteoConnector openMeteoConnector(
            WebClient openMeteoClient
    ) {
        return new OpenMeteoConnector(openMeteoClient);
    }
}
