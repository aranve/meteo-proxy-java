package meteoproxy.api.exceptionhandling;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import meteoproxy.domain.exception.ExternalApiException;
import meteoproxy.domain.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(ValidationException ex) {
        LOG.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorMessage> handleExternalApiException(ExternalApiException ex) {
        LOG.error("External api error: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage("Something went wrong. Try again later."));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ErrorMessage> handleCallNotPermittedException(CallNotPermittedException ex) {
        LOG.error("Circuit breaker is open: {}", ex.getMessage(), ex);
        return ResponseEntity.status(503)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage("Service temporarily unavailable. Please try again later."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleException(RuntimeException ex) {
        LOG.error("Unexpected exception occurred during execution: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage("Something went wrong. Try again later."));
    }
}

