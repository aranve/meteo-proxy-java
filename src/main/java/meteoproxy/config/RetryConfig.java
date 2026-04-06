package meteoproxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class RetryConfig {

    @Bean
    public Retry retry(
            @Value("${retry.max-attempts}") int maxAttempts,
            @Value("${retry.delay-seconds}") long delaySeconds
    ) {
        return Retry.fixedDelay(maxAttempts, Duration.ofSeconds(delaySeconds));
    }

    @Bean
    public ExchangeFilterFunction retryFilter(Retry retry) {
        return (request, next) ->
                Mono.defer(() -> next.exchange(request)
                                .flatMap(response -> {
                                    if (response.statusCode().isError()) {
                                        return response.releaseBody()
                                                .then(Mono.error(new RuntimeException("HTTP error: " + response.statusCode())));
                                    }
                                    return Mono.just(response);
                                }))
                        .retryWhen(retry);
    }
}
