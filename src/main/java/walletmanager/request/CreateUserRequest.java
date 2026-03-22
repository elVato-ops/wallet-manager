package walletmanager.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @Schema(description = "Name of the created user (must not be empty)", example = "Bobek")
        @NotBlank String name)
{
}