package meteoproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MeteoProxyApp {

    static void main(String[] args) {
        SpringApplication.run(MeteoProxyApp.class, args);
    }
}

