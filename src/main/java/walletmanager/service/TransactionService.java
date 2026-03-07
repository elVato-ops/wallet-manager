package walletmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import walletmanager.repository.TransactionRepository;

@Service
@AllArgsConstructor
public class TransactionService
{
    private final TransactionRepository repository;


}
