package walletmanager.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException
{
    public InsufficientFundsException(BigDecimal amount, BigDecimal balance)
    {
        super("The amount " + amount + " is higher than the account balance " + balance);
    }
}
