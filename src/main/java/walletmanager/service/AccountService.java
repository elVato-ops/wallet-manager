package walletmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walletmanager.entity.AccountEntity;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.UserNotFoundException;
import walletmanager.repository.AccountRepository;
import walletmanager.repository.UserRepository;
import walletmanager.response.AccountResponse;
import walletmanager.utils.AccountMapper;

import static walletmanager.utils.AccountMapper.toResponse;

@Service
@AllArgsConstructor
@Transactional
public class AccountService
{
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public Page<AccountResponse> getAccountsForUser(Long id, Pageable pageable)
    {
        if (!userRepository.existsById(id))
        {
            throw new UserNotFoundException(id);
        }

        return accountRepository.findByUserId(id, pageable)
                .map(AccountMapper::toResponse);
    }

    public AccountResponse getAccount(Long id)
    {
        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        return toResponse(account);
    }
}