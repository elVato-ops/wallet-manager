package walletmanager.utils;

import org.springframework.stereotype.Component;
import walletmanager.entity.User;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.UserResponse;

@Component
public class UserMapper
{
    public User toEntity(CreateUserRequest request)
    {
        return new User(request.name());
    }

    public UserResponse toResponse(User user)
    {
        return new UserResponse(user.getId(), user.getName());
    }
}
