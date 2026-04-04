package meteoproxy.config;

import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Response;

import java.time.Duration;

@Configuration
public class RetryConfiguration {

    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.<Response<?>>custom()
                .retryOnResult(response -> !response.isSuccessful())
                .intervalBiFunction((attempt, either) -> Duration.ofSeconds(2).toMillis())
                .build();

        return RetryRegistry.of(config);
    }
}

