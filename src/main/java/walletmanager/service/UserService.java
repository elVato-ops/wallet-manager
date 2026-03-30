package walletmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walletmanager.entity.User;
import walletmanager.exception.UserNotFoundException;
import walletmanager.repository.UserRepository;
import walletmanager.request.CreateAccountRequest;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.AccountResponse;
import walletmanager.response.UserResponse;
import walletmanager.utils.UserMapper;

@Service
@AllArgsConstructor
public class UserService
{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AccountService accountService;

    @Transactional
    public UserResponse createUser(CreateUserRequest request)
    {
        User user = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id)
    {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable)
    {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, Long userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return accountService.createAccount(request, user);
    }

    @Transactional(readOnly = true)
    public Page<AccountResponse> getAccountsForUser(Long id, Pageable pageable)
    {
        if (!userRepository.existsById(id))
        {
            throw new UserNotFoundException(id);
        }

        return accountService.getAccountsForUser(id, pageable);
    }
}
