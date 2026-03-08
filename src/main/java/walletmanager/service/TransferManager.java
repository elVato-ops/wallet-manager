package walletmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walletmanager.entity.AccountEntity;
import walletmanager.entity.TransactionEntity;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.IllegalTransactionException;
import walletmanager.repository.AccountRepository;
import walletmanager.repository.TransactionRepository;
import walletmanager.response.TransactionResponse;

import java.math.BigDecimal;

import static walletmanager.utils.TransactionMapper.toResponse;

@Service
@AllArgsConstructor
public class TransferManager
{
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse transfer(Long fromAccountId, Long toAccountId, BigDecimal amount)
    {
        if (fromAccountId.equals(toAccountId))
        {
            throw new IllegalTransactionException("Cannot transfer to the same account");
        }

        TransactionEntity transactionEntity = performTransaction(fromAccountId, toAccountId, amount);
        transactionRepository.save(transactionEntity);

        return toResponse(transactionEntity);
    }

    private TransactionEntity performTransaction(Long fromAccountId, Long toAccountId, BigDecimal amount)
    {
        AccountEntity fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountId));

        AccountEntity toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException(toAccountId));

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        return new TransactionEntity(amount, fromAccount.getCurrency(), fromAccount, toAccount);
    }
}
