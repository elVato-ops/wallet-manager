package walletmanager.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.UserResponse;
import walletmanager.service.UserService;

import java.net.URI;
import java.util.Set;

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
    public ResponseEntity<Set<UserResponse>> getAllUsers()
    {
        Set<UserResponse> users = userService.getAllUsers();

        return ResponseEntity
                .ok()
                .body(users);
    }
}
