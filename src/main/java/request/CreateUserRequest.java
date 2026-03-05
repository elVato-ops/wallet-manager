package request;

import jakarta.validation.constraints.NotNull;


public record CreateUserRequest(@NotNull String name)
{

}