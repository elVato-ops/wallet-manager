package walletmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Name missing")
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
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "Invalid id format"),
            @ApiResponse(responseCode = "404", description = "User not found")
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
    @GetMapping
    public Page<UserResponse> getAllUsers(Pageable pageable)
    {
        return userService.getAllUsers(pageable);
    }


    @Operation(summary = "Create a new account",
            description = "Creates account for a given user. Fails if the user does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(responseCode = "400", description = "Request invalid, possible reasons include wrong id format, incorrect currency code or negative balance."),
            @ApiResponse(responseCode = "404", description = "User does not exist")
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