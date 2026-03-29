package walletmanager.utils;

import walletmanager.entity.Transaction;
import walletmanager.response.TransactionResponse;

public class TransactionMapper
{
    public static TransactionResponse toResponse(Transaction entity)
    {
        return new TransactionResponse(
                entity.getId(),
                entity.getFromAccount().getId(),
                entity.getToAccount().getId(),
                entity.getCurrency(),
                entity.getAmount());
    }
}