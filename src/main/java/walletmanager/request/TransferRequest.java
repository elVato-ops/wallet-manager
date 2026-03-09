package walletmanager.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(@Positive @NotNull Long fromAccountId, @Positive @NotNull Long toAccountId, @Positive @NotNull BigDecimal amount)
{
}