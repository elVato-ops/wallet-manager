package service;

import request.CreateUserRequest;
import response.UserResponse;
import user.UserEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DomainMapper
{
    public static UserEntity toEntity(CreateUserRequest request)
    {
        return new UserEntity(request.name());
    }

    public static UserResponse toResponse(UserEntity user)
    {
        return new UserResponse(user.getId(), user.getName());
    }

    public static Set<UserResponse> toResponse(List<UserEntity> users)
    {
        return users.stream()
                .map(DomainMapper::toResponse)
                .collect(Collectors.toSet());
    }
}
