package walletmanager.service;

import walletmanager.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import walletmanager.repository.UserRepository;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.UserResponse;
import walletmanager.user.UserEntity;

import java.util.Set;

@Service
@AllArgsConstructor
public class UserService
{
    private final UserRepository repository;

    public UserResponse createUser(CreateUserRequest request)
    {
        UserEntity userEntity = UserMapper.toEntity(request);
        return UserMapper.toResponse(repository.save(userEntity));
    }

    public UserResponse getUser(Long id)
    {
        UserEntity userEntity = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserMapper.toResponse(userEntity);
    }

    public Set<UserResponse> getAllUsers()
    {
        return UserMapper.toResponse(repository.findAll());
    }
}
