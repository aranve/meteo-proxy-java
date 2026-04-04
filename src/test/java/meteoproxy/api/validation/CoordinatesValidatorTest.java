package meteoproxy.api.validation;

import meteoproxy.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesValidatorTest {

    @Test
    void validateLatitudeAllowsValidValues() {
        assertDoesNotThrow(() -> {
            CoordinatesValidator.validateLatitude(new BigDecimal("0"));
            CoordinatesValidator.validateLatitude(new BigDecimal("-90"));
            CoordinatesValidator.validateLatitude(new BigDecimal("90"));
            CoordinatesValidator.validateLatitude(new BigDecimal("45.123"));
        });
    }

    @Test
    void validateLatitudeThrowsExceptionWhenValueIsTooSmall() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                CoordinatesValidator.validateLatitude(new BigDecimal("-90.0001"))
        );
        assertEquals("Latitude must be between -90 and 90", exception.getMessage());
    }

    @Test
    void validateLatitudeThrowsExceptionWhenValueIsTooLarge() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                CoordinatesValidator.validateLatitude(new BigDecimal("90.0001"))
        );
        assertEquals("Latitude must be between -90 and 90", exception.getMessage());
    }

    @Test
    void validateLongitudeAllowsValidValues() {
        assertDoesNotThrow(() -> {
            CoordinatesValidator.validateLongitude(new BigDecimal("0"));
            CoordinatesValidator.validateLongitude(new BigDecimal("-180"));
            CoordinatesValidator.validateLongitude(new BigDecimal("180"));
            CoordinatesValidator.validateLongitude(new BigDecimal("120.456"));
        });
    }

    @Test
    void validateLongitudeThrowsExceptionWhenValueIsTooSmall() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                CoordinatesValidator.validateLongitude(new BigDecimal("-180.0001"))
        );
        assertEquals("Longitude must be between -180 and 180", exception.getMessage());
    }

    @Test
    void validateLongitudeThrowsExceptionWhenValueIsTooLarge() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                CoordinatesValidator.validateLongitude(new BigDecimal("180.0001"))
        );
        assertEquals("Longitude must be between -180 and 180", exception.getMessage());
    }
}

