package walletmanager.exception;

import java.util.Currency;

public class DifferentCurrencyException extends RuntimeException
{
    public DifferentCurrencyException(Currency from, Currency to)
    {
        super("Cannot transfer " + from + " to " + to);
    }
}