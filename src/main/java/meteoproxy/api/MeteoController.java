package meteoproxy.api;

import meteoproxy.api.validation.CoordinatesValidator;
import meteoproxy.domain.meteo.MeteoService;
import meteoproxy.domain.meteo.model.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/meteo/")
public class MeteoController {

    private static final Logger LOG = LoggerFactory.getLogger(MeteoController.class);

    private final MeteoService meteoService;

    public MeteoController(MeteoService meteoService) {
        this.meteoService = meteoService;
    }

    @GetMapping("weather")
    public Weather getCurrentWeather(
            @RequestParam(name = "lat") BigDecimal latitude,
            @RequestParam(name = "lon") BigDecimal longitude
    ) {
        LOG.info("Getting weather for latitude: {}, longitude: {}", latitude, longitude);
        CoordinatesValidator.validateLatitude(latitude);
        CoordinatesValidator.validateLongitude(longitude);
        return meteoService.getCurrentWeather(latitude, longitude);
    }
}

