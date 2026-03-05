package service;

import exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import request.CreateUserRequest;
import response.UserResponse;
import user.UserEntity;

import java.util.Set;

@Service
@AllArgsConstructor
public class UserService
{
    private final UserRepository repository;

    public UserResponse createUser(CreateUserRequest request)
    {
        UserEntity userEntity = DomainMapper.toEntity(request);
        return DomainMapper.toResponse(repository.save(userEntity));
    }

    public UserResponse getUser(Long id)
    {
        UserEntity userEntity = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return DomainMapper.toResponse(userEntity);
    }

    public Set<UserResponse> getAllUsers()
    {
        return DomainMapper.toResponse(repository.findAll());
    }
}
