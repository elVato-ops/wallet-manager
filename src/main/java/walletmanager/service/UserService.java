package walletmanager.service;

import walletmanager.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import walletmanager.repository.AccountRepository;
import walletmanager.repository.UserRepository;
import walletmanager.request.CreateAccountRequest;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.AccountResponse;
import walletmanager.response.UserResponse;
import walletmanager.entity.UserEntity;
import walletmanager.utils.UserMapper;

import java.util.List;

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

    public List<UserResponse> getAllUsers()
    {
        return UserMapper.toResponse(userRepository.findAll());
    }

    public AccountResponse createAccount(CreateAccountRequest request, Long userId)
    {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return toResponse(accountRepository.save(toEntity(request, user)));
    }
}
