package meteoproxy.api.validation;

import meteoproxy.domain.exception.ValidationException;

import java.math.BigDecimal;

public class CoordinatesValidator {

    private static final BigDecimal MIN_LATITUDE = new BigDecimal("-90");
    private static final BigDecimal MAX_LATITUDE = new BigDecimal("90");
    private static final BigDecimal MIN_LONGITUDE = new BigDecimal("-180");
    private static final BigDecimal MAX_LONGITUDE = new BigDecimal("180");

    private CoordinatesValidator() {
    }

    public static void validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        validateLatitude(latitude);
        validateLongitude(longitude);
    }

    static void validateLatitude(BigDecimal latitude) {
        if (latitude == null) {
            throw new ValidationException("Latitude cannot be null");
        }
        if (latitude.compareTo(MIN_LATITUDE) < 0 || latitude.compareTo(MAX_LATITUDE) > 0) {
            throw new ValidationException("Latitude must be between -90 and 90");
        }
    }

    static void validateLongitude(BigDecimal longitude) {
        if (longitude == null) {
            throw new ValidationException("Longitude cannot be null");
        }
        if (longitude.compareTo(MIN_LONGITUDE) < 0 || longitude.compareTo(MAX_LONGITUDE) > 0) {
            throw new ValidationException("Longitude must be between -180 and 180");
        }
    }
}

