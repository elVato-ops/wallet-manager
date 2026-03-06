package walletmanager.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Currency;

public record CreateAccountRequest(@Positive Long userId, @NotNull Currency currency, @PositiveOrZero BigDecimal balance)
{

}