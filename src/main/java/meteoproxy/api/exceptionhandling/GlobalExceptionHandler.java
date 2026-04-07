package meteoproxy.api.exceptionhandling;

import meteoproxy.domain.exception.ExternalApiException;
import meteoproxy.domain.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ErrorMessage>> handleValidationException(ValidationException ex) {
        LOG.warn("Validation error: {}", ex.getMessage());
        return Mono.just(ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(ex.getMessage())));
    }

    @ExceptionHandler(ExternalApiException.class)
    public Mono<ResponseEntity<ErrorMessage>> handleExternalApiException(ExternalApiException ex) {
        LOG.error("External api error: {}", ex.getMessage());
        return Mono.just(ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage("Something went wrong. Try again later.")));
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ErrorMessage>> handleException(RuntimeException ex) {
        LOG.error("Unexpected exception occurred during execution: {}", ex.getMessage());
        return Mono.just(ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage("Something went wrong. Try again later.")));
    }
}

