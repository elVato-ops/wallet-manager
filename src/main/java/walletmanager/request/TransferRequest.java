package walletmanager.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @Schema(description = "Id of the source account", example = "1")
        @Positive @NotNull Long fromAccountId,

        @Schema(description = "Id of the target account", example = "2")
        @Positive @NotNull Long toAccountId,

        @Schema(description = "Amount to transfer (must be positive)", example = "100.50")
        @Positive @NotNull BigDecimal amount)
{
}