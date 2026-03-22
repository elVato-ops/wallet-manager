package walletmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import walletmanager.response.AccountResponse;
import walletmanager.response.TransactionResponse;
import walletmanager.service.AccountService;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@Tag(name = "Accounts", description = "Account operations")
public class AccountController
{
    private final AccountService service;

    @Operation(summary = "Return accounts owned by the user",
                description = "Returns a list of accounts owned by the specified user. Fails if the user does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of accounts",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "User id format invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PageableAsQueryParam
    @GetMapping
    public Page<AccountResponse> getAccountsForUser(
            @Parameter(description = "Id of the account's owner")
            @RequestParam Long userId,
            Pageable pageable)
    {
        return service.getAccountsForUser(userId, pageable);
    }

    @Operation(summary = "Return account with a given id",
                description = "Returns an account with a specified id. Fails if the account does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Account id format invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(
            @Parameter(description = "Id of the account")
            @PathVariable Long id)
    {
        AccountResponse response = service.getAccount(id);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @Operation(summary = "Return all account transactions",
                description = "Returns a list of transactions for a given account. Fails if account does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of transactions",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
    })
    @PageableAsQueryParam
    @GetMapping("/{id}/transactions")
    public Page<TransactionResponse> getTransactions(
            @Parameter(description = "Id of the account")
            @PathVariable Long id,

            Pageable pageable)
    {
        return service.getTransactionsForAccount(id, pageable);
    }
}