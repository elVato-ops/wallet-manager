package walletmanager.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Standard error response")
public record ErrorResponse(

        @Schema(example = "Account with id 1 does not exist")
        String message,

        @Schema(example = "ACCOUNT_NOT_FOUND")
        ErrorCode errorCode,

        @Schema(example = "2026-03-18T12:34:56Z")
        Instant timestamp
){}