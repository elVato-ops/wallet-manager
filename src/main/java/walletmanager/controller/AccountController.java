package walletmanager.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import walletmanager.request.CreateAccountRequest;
import walletmanager.response.AccountResponse;
import walletmanager.service.AccountService;

import java.net.URI;
import java.util.Set;


@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController
{
    private final AccountService service;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request)
    {
        AccountResponse account = service.createAccount(request);

        return ResponseEntity
                .created(URI.create("/accounts/" + account.id()))
                .body(account);
    }

    @GetMapping
    public ResponseEntity<Set<AccountResponse>> obtainAccountsForUser(@RequestParam Long userId)
    {
        Set<AccountResponse> accountResponses = service.obtainAccountsForUser(userId);

        return ResponseEntity
                .ok()
                .body(accountResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> obtainAccount(@PathVariable Long id)
    {
        AccountResponse response = service.obtainAccount(id);

        return ResponseEntity
                .ok()
                .body(response);
    }
}