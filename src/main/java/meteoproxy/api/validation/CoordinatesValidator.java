package meteoproxy.api.validation;

import meteoproxy.domain.exception.ValidationException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class CoordinatesValidator {

    private static final BigDecimal MIN_LATITUDE = new BigDecimal("-90");
    private static final BigDecimal MAX_LATITUDE = new BigDecimal("90");
    private static final BigDecimal MIN_LONGITUDE = new BigDecimal("-180");
    private static final BigDecimal MAX_LONGITUDE = new BigDecimal("180");

    private CoordinatesValidator() {
    }

    public static Mono<Void> validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        return validateLatitude(latitude)
                .then(validateLongitude(longitude));
    }

    static Mono<Void> validateLatitude(BigDecimal latitude) {
        if (latitude.compareTo(MIN_LATITUDE) < 0 || latitude.compareTo(MAX_LATITUDE) > 0) {
            return Mono.error(new ValidationException("Latitude must be between -90 and 90"));
        }
        return Mono.empty();
    }

    static Mono<Void> validateLongitude(BigDecimal longitude) {
        if (longitude.compareTo(MIN_LONGITUDE) < 0 || longitude.compareTo(MAX_LONGITUDE) > 0) {
            return Mono.error(new ValidationException("Longitude must be between -180 and 180"));
        }
        return Mono.empty();
    }
}

