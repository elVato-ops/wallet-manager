package walletmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import walletmanager.entity.Transaction;
import walletmanager.repository.TransactionRepository;
import walletmanager.response.TransactionResponse;
import walletmanager.utils.TransactionMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static walletmanager.utils.TestConstants.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest
{
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Spy
    private TransactionMapper transactionMapper;

    @Nested
    class CreateTransaction
    {
        @Test
        public void returnsTransaction_whenSuccess()
        {
            //GIVEN
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction());
            ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

            //WHEN
            transactionService.createTransaction(transaction());

            //THEN
            verify(transactionRepository).save(captor.capture());
            verifyNoMoreInteractions(transactionRepository);

            Transaction transaction = captor.getValue();
            assertEquals(transaction().getAmount(), transaction.getAmount());
            assertEquals(transaction().getCurrency(), transaction.getCurrency());
            assertEquals(transaction().getFromAccount().getId(), transaction.getFromAccount().getId());
            assertEquals(transaction().getToAccount().getId(), transaction.getToAccount().getId());
        }
    }

    @Nested
    class FindForAccount
    {
        @Test
        public void returnsTransaction_whenSuccess()
        {
            //GIVEN
            when(transactionRepository.findTransactionsForAccount(ACCOUNT_ID, PAGEABLE)).thenReturn(transactionPageResponse());

            //WHEN
            Page<TransactionResponse> page = transactionService.findTransactionsForAccount(ACCOUNT_ID, PAGEABLE);

            //THEN
            verify(transactionRepository, times(1)).findTransactionsForAccount(ACCOUNT_ID, PAGEABLE);
            verifyNoMoreInteractions(transactionRepository);

            List<TransactionResponse> responses = page.get().toList();
            assertEquals(1, responses.size());

            TransactionResponse response = responses.get(0);
            assertEquals(transaction().getFromAccount().getId(), response.fromAccountId());
            assertEquals(transaction().getToAccount().getId(), response.toAccountId());
            assertEquals(transaction().getCurrency(), response.currency());
            assertEquals(transaction().getAmount(), response.amount());
        }
    }
}
