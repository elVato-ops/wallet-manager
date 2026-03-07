package walletmanager.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import walletmanager.request.CreateAccountRequest;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.AccountResponse;
import walletmanager.response.UserResponse;
import walletmanager.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController
{
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request)
    {
        UserResponse response = userService.createUser(request);

        return ResponseEntity
                .created(URI.create("/users/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id)
    {
        UserResponse user = userService.getUser(id);

        return ResponseEntity
                .ok()
                .body(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers()
    {
        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity
                .ok()
                .body(users);
    }

    @PostMapping("/{userId}/accounts")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request, @PathVariable @Positive Long userId)
    {
        AccountResponse account = userService.createAccount(request, userId);

        return ResponseEntity
                .created(URI.create("/users/" + account.userId() + "/accounts/" + account.id()))
                .body(account);
    }
}