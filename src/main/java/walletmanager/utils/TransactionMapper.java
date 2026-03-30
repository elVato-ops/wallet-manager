package walletmanager.utils;

import org.springframework.stereotype.Component;
import walletmanager.entity.Transaction;
import walletmanager.response.TransactionResponse;

@Component
public class TransactionMapper
{
    public TransactionResponse toResponse(Transaction entity)
    {
        return new TransactionResponse(
                entity.getId(),
                entity.getFromAccount().getId(),
                entity.getToAccount().getId(),
                entity.getCurrency(),
                entity.getAmount());
    }
}