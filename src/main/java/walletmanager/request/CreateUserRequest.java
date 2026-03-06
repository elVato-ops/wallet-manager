package walletmanager.request;

import jakarta.validation.constraints.NotEmpty;


public record CreateUserRequest(@NotEmpty String name)
{

}