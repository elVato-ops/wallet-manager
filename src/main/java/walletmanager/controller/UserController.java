package walletmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import walletmanager.request.CreateAccountRequest;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.AccountResponse;
import walletmanager.response.UserResponse;
import walletmanager.service.UserService;

import java.net.URI;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "Users", description = "User operations")
public class UserController
{
    private final UserService userService;

    @Operation(summary = "Create a new account user",
                description = "Creates a new user based on request. Fails if name is empty.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Name missing",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request)
    {
        UserResponse response = userService.createUser(request);

        return ResponseEntity
                .created(URI.create("/users/" + response.id()))
                .body(response);
    }

    @Operation(summary = "Return user with a given id",
                description = "Returns a user with id specified in path. Fails if the user does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid id format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "Id of the user")
            @PathVariable Long id)
    {
        UserResponse user = userService.getUser(id);

        return ResponseEntity
                .ok()
                .body(user);
    }

    @Operation(summary = "Return all users",
                description = "Returns all existing users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of accounts")
    })
    @PageableAsQueryParam
    @GetMapping
    public Page<UserResponse> getAllUsers(Pageable pageable)
    {
        return userService.getAllUsers(pageable);
    }

    @Operation(summary = "Create a new account",
            description = "Creates account for a given user. Fails if the user does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Request invalid, possible reasons include wrong id format, incorrect currency code or negative balance.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{userId}/accounts")
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @Parameter(description = "Id of the account owner")
            @PathVariable @Positive Long userId)
    {
        AccountResponse account = userService.createAccount(request, userId);

        return ResponseEntity
                .created(URI.create("/users/" + account.userId() + "/accounts/" + account.id()))
                .body(account);
    }
}