package walletmanager.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Currency;

public record CreateAccountRequest(
        @Schema(description = "Account currency code", example = "PLN")
        @NotNull Currency currency,

        @Schema(description = "Account initial balance (must not be negative)", example = "100.50")
        @PositiveOrZero BigDecimal balance)
{
}