package walletmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walletmanager.entity.AccountEntity;
import walletmanager.entity.TransactionEntity;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.DifferentCurrencyException;
import walletmanager.exception.IllegalTransactionException;
import walletmanager.repository.AccountRepository;
import walletmanager.repository.TransactionRepository;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class TransferManager
{
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionEntity transfer(Long fromAccountId, Long toAccountId, BigDecimal amount)
    {
        if (fromAccountId.equals(toAccountId))
        {
            throw new IllegalTransactionException("Cannot transfer to the same account");
        }

        TransactionEntity transactionEntity = performTransaction(fromAccountId, toAccountId, amount);
        transactionRepository.save(transactionEntity);

        return transactionEntity;
    }

    private TransactionEntity performTransaction(Long fromAccountId, Long toAccountId, BigDecimal amount)
    {
        AccountEntity fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountId));

        AccountEntity toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException(toAccountId));

        if (!fromAccount.hasCurrency(toAccount.getCurrency()))
        {
            throw new DifferentCurrencyException(fromAccount.getCurrency(), toAccount.getCurrency());
        }

        return TransactionEntity.transfer(fromAccount, toAccount, amount);
    }
}
