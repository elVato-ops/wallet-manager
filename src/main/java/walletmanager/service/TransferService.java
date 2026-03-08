package walletmanager.service;

import jakarta.persistence.OptimisticLockException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import walletmanager.exception.InsufficientFundsException;
import walletmanager.request.TransferRequest;
import walletmanager.response.TransactionResponse;

@Service
@AllArgsConstructor
public class TransferService
{
    private final TransferManager transferManager;

    public TransactionResponse transfer(TransferRequest request)
    {
        int retries = 3;
        while(retries > 0)
        {
            try
            {
                return transferManager.transfer(request.fromAccountId(), request.toAccountId(), request.amount());
            }
            catch (OptimisticLockException e)
            {
                retries--;
            }
        }

        throw new InsufficientFundsException();
    }
}