package walletmanager.response;

import java.math.BigDecimal;
import java.util.Currency;

public record AccountResponse(Long id, Currency currency, BigDecimal balance, Long userId)
{
}
