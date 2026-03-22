package walletmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import walletmanager.exception.ErrorResponse;
import walletmanager.request.TransferRequest;
import walletmanager.response.TransactionResponse;
import walletmanager.service.TransferService;

import java.net.URI;

@RestController
@RequestMapping("/transfers")
@AllArgsConstructor
@Tag(name = "Transfers", description = "Money transfer operations")
public class TransferController
{
    private final TransferService service;

    @Operation(summary = "Transfer money between accounts",
                description = "Transfers a specified amount from one account to another. Fails if balance is insufficient or currency doesn't match.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transfer successful",
                content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Request invalid",
                    content = @Content(schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = org.springframework.web.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Business validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})

    @PostMapping
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request)
    {
        TransactionResponse response = service.transfer(request);

        return ResponseEntity
                .created(URI.create("/transfers/" + response.id()))
                .body(response);
    }
}