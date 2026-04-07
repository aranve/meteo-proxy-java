package meteoproxy.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import meteoproxy.domain.meteo.model.Location;

import java.math.BigDecimal;

@Schema(description = "Geographic coordinates of a location")
public record LocationDto(
        @Schema(description = "Latitude in decimal degrees", example = "52.52", minimum = "-90", maximum = "90")
        BigDecimal lat,
        @Schema(description = "Longitude in decimal degrees", example = "13.41", minimum = "-180", maximum = "180")
        BigDecimal lon
) {
    public static LocationDto from(Location location) {
        return new LocationDto(location.lat(), location.lon());
    }
}
