package walletmanager.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import walletmanager.request.TransferRequest;
import walletmanager.response.TransactionResponse;
import walletmanager.service.TransferService;

import java.net.URI;

@RestController
@RequestMapping("/transfers")
@AllArgsConstructor
public class TransferController
{
    private final TransferService service;

    @PostMapping
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request)
    {
        TransactionResponse response = service.transfer(request);

        return ResponseEntity
                .created(URI.create("/transfers/" + response.id()))
                .body(response);
    }
}