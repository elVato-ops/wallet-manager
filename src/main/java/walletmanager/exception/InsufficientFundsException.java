package walletmanager.exception;

public class InsufficientFundsException extends RuntimeException
{
    public InsufficientFundsException()
    {
        super("You have insufficient funds to perform this operation");
    }
}