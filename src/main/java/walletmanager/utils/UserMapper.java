package walletmanager.utils;

import walletmanager.request.CreateUserRequest;
import walletmanager.response.UserResponse;
import walletmanager.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper
{
    public static UserEntity toEntity(CreateUserRequest request)
    {
        return new UserEntity(request.name());
    }

    public static UserResponse toResponse(UserEntity user)
    {
        return new UserResponse(user.getId(), user.getName());
    }

    public static List<UserResponse> toResponse(List<UserEntity> users)
    {
        return users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }
}
