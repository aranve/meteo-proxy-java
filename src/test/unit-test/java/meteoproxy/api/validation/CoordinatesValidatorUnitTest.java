package meteoproxy.api.validation;

import meteoproxy.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class CoordinatesValidatorUnitTest {

    @Test
    void validateLatitudeAllowsValidValues() {
        StepVerifier.create(CoordinatesValidator.validateLatitude(new BigDecimal("0")))
                .verifyComplete();

        StepVerifier.create(CoordinatesValidator.validateLatitude(new BigDecimal("-90")))
                .verifyComplete();

        StepVerifier.create(CoordinatesValidator.validateLatitude(new BigDecimal("90")))
                .verifyComplete();

        StepVerifier.create(CoordinatesValidator.validateLatitude(new BigDecimal("45.123")))
                .verifyComplete();
    }

    @Test
    void validateLatitudeThrowsExceptionWhenValueIsTooSmall() {
        StepVerifier.create(CoordinatesValidator.validateLatitude(new BigDecimal("-90.0001")))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("Latitude must be between -90 and 90"))
                .verify();
    }

    @Test
    void validateLatitudeThrowsExceptionWhenValueIsTooLarge() {
        StepVerifier.create(CoordinatesValidator.validateLatitude(new BigDecimal("90.0001")))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("Latitude must be between -90 and 90"))
                .verify();
    }

    @Test
    void validateLongitudeAllowsValidValues() {
        StepVerifier.create(CoordinatesValidator.validateLongitude(new BigDecimal("0")))
                .verifyComplete();

        StepVerifier.create(CoordinatesValidator.validateLongitude(new BigDecimal("-180")))
                .verifyComplete();

        StepVerifier.create(CoordinatesValidator.validateLongitude(new BigDecimal("180")))
                .verifyComplete();

        StepVerifier.create(CoordinatesValidator.validateLongitude(new BigDecimal("120.456")))
                .verifyComplete();
    }

    @Test
    void validateLongitudeThrowsExceptionWhenValueIsTooSmall() {
        StepVerifier.create(CoordinatesValidator.validateLongitude(new BigDecimal("-180.0001")))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("Longitude must be between -180 and 180"))
                .verify();
    }

    @Test
    void validateLongitudeThrowsExceptionWhenValueIsTooLarge() {
        StepVerifier.create(CoordinatesValidator.validateLongitude(new BigDecimal("180.0001")))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("Longitude must be between -180 and 180"))
                .verify();
    }

    @Test
    void validateCoordinatesAllowsValidValues() {
        StepVerifier.create(CoordinatesValidator.validateCoordinates(
                        new BigDecimal("45.123"),
                        new BigDecimal("120.456")))
                .verifyComplete();
    }

    @Test
    void validateCoordinatesRejectsInvalidLatitude() {
        StepVerifier.create(CoordinatesValidator.validateCoordinates(
                        new BigDecimal("-90.0001"),
                        new BigDecimal("120.456")))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("Latitude must be between -90 and 90"))
                .verify();
    }

    @Test
    void validateCoordinatesRejectsInvalidLongitude() {
        StepVerifier.create(CoordinatesValidator.validateCoordinates(
                        new BigDecimal("45.123"),
                        new BigDecimal("180.0001")))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("Longitude must be between -180 and 180"))
                .verify();
    }
}

