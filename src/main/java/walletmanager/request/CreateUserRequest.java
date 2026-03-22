package walletmanager.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;


public record CreateUserRequest(
        @Schema(description = "Name of the created user (must not be empty)", example = "Bobek")
        @NotEmpty String name)
{
}