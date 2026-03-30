package walletmanager.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User data returned after a successful operation")
public record UserResponse(
        @Schema(description = "User id", example = "123")
        Long id,

        @Schema(description = "User name", example = "Bobek")
        String name)
{
}