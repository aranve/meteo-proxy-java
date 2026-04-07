package meteoproxy.api.exceptionhandling;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response message")
public record ErrorMessage(
        String message
) {
}

