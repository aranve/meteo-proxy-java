package meteoproxy.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.okhttp.LogbookInterceptor;

import java.util.concurrent.TimeUnit;

@Configuration
public class HttpConfig {

    @Bean
    public OkHttpClient okHttpClient(Logbook logbook) {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new LogbookInterceptor(logbook))
                .readTimeout(2, TimeUnit.SECONDS)
                .build();
    }
}

