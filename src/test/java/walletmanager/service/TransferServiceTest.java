package walletmanager.service;

import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import walletmanager.exception.InsufficientFundsException;
import walletmanager.response.TransactionResponse;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static walletmanager.utils.TestConstants.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest
{
    @InjectMocks
    private TransferService service;

    @Mock
    private TransferManager transferManager;

    @Test
    public void returnResponse_whenRequestValid()
    {
        //GIVEN
        when(transferManager.transfer(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, TRANSFER_AMOUNT))
                .thenReturn(transactionResponse());

        //WHEN
        TransactionResponse response = service.transfer(transferRequest());

        //THEN
        verify(transferManager, times(1)).transfer(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, TRANSFER_AMOUNT);
        verifyNoMoreInteractions(transferManager);

        assertEquals(TRANSACTION_ID, response.id());
        assertEquals(FROM_ACCOUNT_ID, response.fromAccountId());
        assertEquals(TO_ACCOUNT_ID, response.toAccountId());
        assertEquals(PLN, response.currency());
        assertEquals(TRANSFER_AMOUNT, response.amount());
    }

    @Test
    public void returnResponse_afterTwoRetries()
    {
        //GIVEN
        when(transferManager.transfer(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, TRANSFER_AMOUNT))
                .thenThrow(OptimisticLockException.class)
                .thenThrow(OptimisticLockException.class)
                .thenReturn(transactionResponse());

        //WHEN
        TransactionResponse response = service.transfer(transferRequest());

        //THEN
        verify(transferManager, times(3)).transfer(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, TRANSFER_AMOUNT);
        verifyNoMoreInteractions(transferManager);

        assertEquals(TRANSACTION_ID, response.id());
        assertEquals(FROM_ACCOUNT_ID, response.fromAccountId());
        assertEquals(TO_ACCOUNT_ID, response.toAccountId());
        assertEquals(PLN, response.currency());
        assertEquals(TRANSFER_AMOUNT, response.amount());
    }

    @Test
    public void throwInsufficientFunds_whenRetryFails()
    {
        //GIVEN
        when(transferManager.transfer(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, TRANSFER_AMOUNT))
                .thenThrow(OptimisticLockException.class);

        //WHEN
        assertThrows(InsufficientFundsException.class, () -> service.transfer(transferRequest()));
    }
}