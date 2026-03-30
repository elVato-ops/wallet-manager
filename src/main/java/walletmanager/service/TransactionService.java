package walletmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walletmanager.entity.Transaction;
import walletmanager.repository.TransactionRepository;
import walletmanager.response.TransactionResponse;
import walletmanager.utils.TransactionMapper;

@Service
@AllArgsConstructor
public class TransactionService
{
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> findTransactionsForAccount(Long id, Pageable pageable)
    {
        return transactionRepository.findTransactionsForAccount(id, pageable)
                .map(transactionMapper::toResponse);
    }

    @Transactional
    public TransactionResponse createTransaction(Transaction transaction)
    {
        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }
}