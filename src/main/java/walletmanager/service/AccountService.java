package walletmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import walletmanager.entity.AccountEntity;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.UserNotFoundException;
import walletmanager.repository.AccountRepository;
import walletmanager.repository.UserRepository;
import walletmanager.request.CreateAccountRequest;
import walletmanager.response.AccountResponse;
import walletmanager.entity.UserEntity;

import java.util.List;

import static walletmanager.utils.AccountMapper.toEntity;
import static walletmanager.utils.AccountMapper.toResponse;

@Service
@AllArgsConstructor
public class AccountService
{
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AccountResponse createAccount(CreateAccountRequest request)
    {
        UserEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        return toResponse(accountRepository.save(toEntity(request, user)));
    }

    public List<AccountResponse> obtainAccountsForUser(Long id)
    {
        if (!userRepository.existsById(id))
        {
            throw new UserNotFoundException(id);
        }

        return toResponse(accountRepository.findByUserId(id));
    }

    public AccountResponse obtainAccount(Long id)
    {
        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        return toResponse(account);
    }
}