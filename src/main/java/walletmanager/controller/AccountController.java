package walletmanager.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import walletmanager.response.AccountResponse;
import walletmanager.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController
{
    private final AccountService service;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> obtainAccountsForUser(@RequestParam Long userId)
    {
        List<AccountResponse> accountResponses = service.obtainAccountsForUser(userId);

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