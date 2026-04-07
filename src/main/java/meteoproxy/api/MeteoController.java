package meteoproxy.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import meteoproxy.api.dto.WeatherDto;
import meteoproxy.api.validation.CoordinatesValidator;
import meteoproxy.domain.meteo.MeteoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/meteo/")
@Tag(name = "MeteoController", description = "Endpoint for retrieving weather information")
public class MeteoController {

    private static final Logger LOG = LoggerFactory.getLogger(MeteoController.class);

    private final MeteoService meteoService;

    public MeteoController(MeteoService meteoService) {
        this.meteoService = meteoService;
    }

    @Operation(
            summary = "Get current weather",
            description = "Retrieves current weather information for the specified geographic coordinates. " +
                    "Coordinates are validated and rounded to 2 decimal places before querying the external API."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved weather data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid coordinates provided (latitude must be between -90 and 90, longitude must be between -180 and 180)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error or external API failure",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("weather")
    public Mono<WeatherDto> getCurrentWeather(
            @Parameter(description = "Latitude coordinate (must be between -90 and 90)", required = true, example = "52.52")
            @RequestParam(name = "lat") BigDecimal latitude,
            @Parameter(description = "Longitude coordinate (must be between -180 and 180)", required = true, example = "13.41")
            @RequestParam(name = "lon") BigDecimal longitude
    ) {
        LOG.info("Getting weather for latitude: {}, longitude: {}", latitude, longitude);
        return CoordinatesValidator.validateCoordinates(latitude, longitude)
                .then(Mono.defer(() -> meteoService.getCurrentWeather(latitude, longitude)))
                .map(WeatherDto::from);
    }
}

