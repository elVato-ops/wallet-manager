package walletmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walletmanager.entity.Account;
import walletmanager.entity.User;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.repository.AccountRepository;
import walletmanager.request.CreateAccountRequest;
import walletmanager.response.AccountResponse;
import walletmanager.response.TransactionResponse;
import walletmanager.utils.AccountMapper;

@Service
@AllArgsConstructor
@Transactional
public class AccountService
{
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransactionService transactionService;

    public Page<AccountResponse> getAccountsForUser(Long id, Pageable pageable)
    {
        return accountRepository.findByUserId(id, pageable)
                .map(accountMapper::toResponse);
    }

    public AccountResponse getAccount(Long id)
    {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        return accountMapper.toResponse(account);
    }

    public Page<TransactionResponse> getTransactionsForAccount(Long id, Pageable pageable)
    {
        if (!accountRepository.existsById(id))
        {
            throw new AccountNotFoundException(id);
        }

        return transactionService.findTransactionsForAccount(id, pageable);
    }

    public AccountResponse createAccount(CreateAccountRequest request, User user)
    {
        return accountMapper
                .toResponse(accountRepository.save(accountMapper.toEntity(request, user)));
    }
}