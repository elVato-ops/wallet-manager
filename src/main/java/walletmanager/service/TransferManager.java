package walletmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walletmanager.entity.Account;
import walletmanager.entity.Transaction;
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
    public Transaction transfer(Long fromAccountId, Long toAccountId, BigDecimal amount)
    {
        if (fromAccountId.equals(toAccountId))
        {
            throw new IllegalTransactionException("Cannot transfer to the same account");
        }

        Transaction transaction = performTransaction(fromAccountId, toAccountId, amount);
        transactionRepository.save(transaction);

        return transaction;
    }

    private Transaction performTransaction(Long fromAccountId, Long toAccountId, BigDecimal amount)
    {
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountId));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException(toAccountId));

        if (!fromAccount.hasCurrency(toAccount.getCurrency()))
        {
            throw new DifferentCurrencyException(fromAccount.getCurrency(), toAccount.getCurrency());
        }

        return Transaction.transfer(fromAccount, toAccount, amount);
    }
}
