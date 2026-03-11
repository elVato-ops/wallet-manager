package walletmanager.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import walletmanager.response.AccountResponse;
import walletmanager.response.TransactionResponse;
import walletmanager.service.AccountService;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController
{
    private final AccountService service;

    @GetMapping
    public Page<AccountResponse> getAccountsForUser(@RequestParam Long userId, Pageable pageable)
    {
        return service.getAccountsForUser(userId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id)
    {
        AccountResponse response = service.getAccount(id);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @GetMapping("/{id}/transactions")
    public Page<TransactionResponse> getTransactions(@PathVariable Long id, Pageable pageable)
    {
        return service.getTransactionsForAccount(id, pageable);
    }
}