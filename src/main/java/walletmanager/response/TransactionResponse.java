package walletmanager.response;

import java.math.BigDecimal;
import java.util.Currency;

public record TransactionResponse(Long id, Long accountFromId, Long accountToId, Currency currency, BigDecimal amount)
{
}
