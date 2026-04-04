package meteoproxy.connector.openmeteo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.RetryRegistry;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class OpenMeteoConfig {

    @Bean
    public OpenMeteoClient openMeteoClient(
            @Value("${external.open-meteo-api.url}") String url,
            ObjectMapper objectMapper,
            OkHttpClient okHttpClient
    ) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(okHttpClient)
                .build()
                .create(OpenMeteoClient.class);
    }

    @Bean
    public OpenMeteoConnector openMeteoConnector(OpenMeteoClient openMeteoClient, RetryRegistry retryRegistry) {
        return new OpenMeteoConnector(
                openMeteoClient,
                retryRegistry.retry("openMeteoConnector")
        );
    }
}

