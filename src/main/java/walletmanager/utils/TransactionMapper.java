package walletmanager.utils;

import walletmanager.entity.TransactionEntity;
import walletmanager.response.TransactionResponse;

public class TransactionMapper
{
    public static TransactionResponse toResponse(TransactionEntity entity)
    {
        return new TransactionResponse(
                entity.getId(),
                entity.getFromAccount().getId(),
                entity.getToAccount().getId(),
                entity.getCurrency(),
                entity.getAmount());
    }
}
