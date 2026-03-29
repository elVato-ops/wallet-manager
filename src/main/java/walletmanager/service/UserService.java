package walletmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walletmanager.entity.User;
import walletmanager.exception.UserNotFoundException;
import walletmanager.repository.AccountRepository;
import walletmanager.repository.UserRepository;
import walletmanager.request.CreateAccountRequest;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.AccountResponse;
import walletmanager.response.UserResponse;
import walletmanager.utils.UserMapper;

import static walletmanager.utils.AccountMapper.toEntity;
import static walletmanager.utils.AccountMapper.toResponse;

@Service
@AllArgsConstructor
public class UserService
{
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request)
    {
        User user = UserMapper.toEntity(request);
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id)
    {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable)
    {
        return userRepository.findAll(pageable)
                .map(UserMapper::toResponse);
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, Long userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return toResponse(accountRepository.save(toEntity(request, user)));
    }
}
