package meteoproxy.api.validation;

import meteoproxy.domain.exception.ValidationException;
import org.apache.commons.validator.routines.BigDecimalValidator;

import java.math.BigDecimal;

public class CoordinatesValidator {

    private CoordinatesValidator() {}

    public static void validateLatitude(BigDecimal latitude) {
        BigDecimalValidator validator = BigDecimalValidator.getInstance();
        if (!validator.isInRange(latitude, -90, 90)) {
            throw new ValidationException("Latitude must be between -90 and 90");
        }
    }

    public static void validateLongitude(BigDecimal longitude) {
        BigDecimalValidator validator = BigDecimalValidator.getInstance();
        if (!validator.isInRange(longitude, -180, 180)) {
            throw new ValidationException("Longitude must be between -180 and 180");
        }
    }
}

