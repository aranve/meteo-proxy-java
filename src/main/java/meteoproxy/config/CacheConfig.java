package meteoproxy.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import meteoproxy.connector.openmeteo.ForecastCacheKey;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<ForecastCacheKey, Mono<GetForecastResponse>> forecastCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .maximumSize(5000)
                .build();
    }
}
