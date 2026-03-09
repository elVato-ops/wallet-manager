package walletmanager.exception;

public class ConcurrentTransferException extends RuntimeException
{
    public ConcurrentTransferException(String message)
    {
        super(message);
    }
}
