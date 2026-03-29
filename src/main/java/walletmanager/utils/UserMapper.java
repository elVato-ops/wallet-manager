package walletmanager.utils;

import walletmanager.entity.User;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.UserResponse;

public class UserMapper
{
    public static User toEntity(CreateUserRequest request)
    {
        return new User(request.name());
    }

    public static UserResponse toResponse(User user)
    {
        return new UserResponse(user.getId(), user.getName());
    }
}
