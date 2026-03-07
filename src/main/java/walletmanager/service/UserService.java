package walletmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import walletmanager.entity.UserEntity;
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

    public UserResponse createUser(CreateUserRequest request)
    {
        UserEntity userEntity = UserMapper.toEntity(request);
        return UserMapper.toResponse(userRepository.save(userEntity));
    }

    public UserResponse getUser(Long id)
    {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserMapper.toResponse(userEntity);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable)
    {
        return userRepository.findAll(pageable)
                .map(UserMapper::toResponse);
    }

    public AccountResponse createAccount(CreateAccountRequest request, Long userId)
    {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return toResponse(accountRepository.save(toEntity(request, user)));
    }
}
