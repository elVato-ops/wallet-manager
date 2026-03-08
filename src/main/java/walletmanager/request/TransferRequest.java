package walletmanager.request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(@Positive Long fromAccountId, @Positive Long toAccountId, @Positive BigDecimal amount)
{
}