package walletmanager.response;

import java.math.BigDecimal;
import java.util.Currency;

public record TransactionResponse(Long id, Long fromAccountId, Long toAccountId, Currency currency, BigDecimal amount)
{
}
