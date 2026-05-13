package meteoproxy.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import meteoproxy.connector.openmeteo.ForecastCacheKey;
import meteoproxy.connector.openmeteo.dto.GetForecastResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<ForecastCacheKey, GetForecastResponse> forecastCache(
            @Value("${cache.forecast.ttl-seconds}") int ttlSeconds,
            @Value("${cache.forecast.max-size}") int maxSize
    ) {
        return Caffeine.newBuilder()
                .expireAfterWrite(ttlSeconds, TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .build();
    }
}
