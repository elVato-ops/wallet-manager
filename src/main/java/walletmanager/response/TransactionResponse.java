package walletmanager.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Currency;

@Schema(description = "Transfer result returned after successful operation")
public record TransactionResponse(
        @Schema(description = "Transaction id", example = "123")
        Long id,

        @Schema(description = "Source account id", example = "1")
        Long fromAccountId,

        @Schema(description = "Target account id", example = "2")
        Long toAccountId,

        @Schema(description = "Transaction currency code", example = "PLN")
        Currency currency,

        @Schema(description = "Transfer amount", example = "100.50")
        BigDecimal amount)
{
}
