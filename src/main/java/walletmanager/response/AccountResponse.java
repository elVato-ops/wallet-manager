package walletmanager.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Currency;

@Schema(description = "Account data returned after a successful operation")
public record AccountResponse(
        @Schema(description = "Account id", example = "123")
        Long id,

        @Schema(description = "Account currency", example = "PLN")
        Currency currency,

        @Schema(description = "Account balance", example = "100.50")
        BigDecimal balance,

        @Schema(description = "Account owner id", example = "992")
        Long userId)
{
}