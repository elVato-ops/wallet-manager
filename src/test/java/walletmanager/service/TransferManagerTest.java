package walletmanager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import walletmanager.entity.AccountEntity;
import walletmanager.entity.TransactionEntity;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.DifferentCurrencyException;
import walletmanager.exception.IllegalTransactionException;
import walletmanager.exception.InsufficientFundsException;
import walletmanager.repository.AccountRepository;
import walletmanager.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static walletmanager.utils.TestConstants.*;

@ExtendWith(MockitoExtension.class)
public class TransferManagerTest
{
    @InjectMocks
    private TransferManager transferManager;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    public void returnsEntity_whenInputValid()
    {
        //GIVEN
        AccountEntity fromAccount = new AccountEntity(PLN, BigDecimal.valueOf(120), user());
        when(accountRepository.findById(FROM_ACCOUNT_ID)).thenReturn(Optional.of(fromAccount));

        AccountEntity toAccount = new AccountEntity(PLN, BigDecimal.valueOf(30), user());
        when(accountRepository.findById(TO_ACCOUNT_ID)).thenReturn(Optional.of(toAccount));

        //WHEN
        TransactionEntity entity = transferManager.transfer(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, TRANSFER_AMOUNT);

        //THEN
        verify(accountRepository, times(2)).findById(anyLong());
        verifyNoMoreInteractions(accountRepository);
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        verifyNoMoreInteractions(transactionRepository);

        assertEquals(BigDecimal.valueOf(70), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(80), toAccount.getBalance());
        assertEquals(TRANSFER_AMOUNT, entity.getAmount());
        assertEquals(PLN, entity.getCurrency());
    }

    @Test
    public void throwsIllegalTransaction_whenSameAccounts()
    {
        //WHEN
        assertThrows(IllegalTransactionException.class, () -> transferManager.transfer(FROM_ACCOUNT_ID, FROM_ACCOUNT_ID, TRANSFER_AMOUNT));

        //THEN
        verifyNoInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    public void throwsAccountNotFound_whenAccountNotExists()
    {
        //GIVEN
        when(accountRepository.findById(FROM_ACCOUNT_ID)).thenThrow(new AccountNotFoundException(FROM_ACCOUNT_ID));

        //WHEN
        assertThrows(AccountNotFoundException.class, () -> transferManager.transfer(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, TRANSFER_AMOUNT));

        //THEN
        verify(accountRepository, times(1)).findById(FROM_ACCOUNT_ID);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    public void throwsIllegalTransaction_whenNegativeAmount()
    {
        //GIVEN
        AccountEntity fromAccount = new AccountEntity(PLN, BigDecimal.valueOf(120), user());
        when(accountRepository.findById(FROM_ACCOUNT_ID)).thenReturn(Optional.of(fromAccount));

        AccountEntity toAccount = new AccountEntity(PLN, BigDecimal.valueOf(30), user());
        when(accountRepository.findById(TO_ACCOUNT_ID)).thenReturn(Optional.of(toAccount));

        //WHEN
        assertThrows(IllegalTransactionException.class, () -> transferManager.transfer(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, BigDecimal.valueOf(-50)));

        //THEN
        verify(accountRepository, times(2)).findById(anyLong());
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);

        assertEquals(BigDecimal.valueOf(120), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(30), toAccount.getBalance());
    }

    @Test
    public void throwsInsufficientFunds_whenBalanceLowerThanAmount()
    {
        //GIVEN
        AccountEntity fromAccount = new AccountEntity(PLN, BigDecimal.valueOf(120), user());
        when(accountRepository.findById(FROM_ACCOUNT_ID)).thenReturn(Optional.of(fromAccount));

        AccountEntity toAccount = new AccountEntity(PLN, BigDecimal.valueOf(30), user());
        when(accountRepository.findById(TO_ACCOUNT_ID)).thenReturn(Optional.of(toAccount));

        //WHEN
        assertThrows(InsufficientFundsException.class, () -> transferManager.transfer(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, BigDecimal.valueOf(1000L)));

        //THEN
        verify(accountRepository, times(2)).findById(anyLong());
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);

        assertEquals(BigDecimal.valueOf(120), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(30), toAccount.getBalance());
    }

    @Test
    public void throwDifferentCurrency_ifDifferentCurrencies()
    {
        AccountEntity fromAccount = new AccountEntity(PLN, BigDecimal.valueOf(120), user());
        when(accountRepository.findById(FROM_ACCOUNT_ID)).thenReturn(Optional.of(fromAccount));

        AccountEntity toAccount = new AccountEntity(EUR, BigDecimal.valueOf(30), user());
        when(accountRepository.findById(TO_ACCOUNT_ID)).thenReturn(Optional.of(toAccount));

        //WHEN
        assertThrows(DifferentCurrencyException.class, () -> transferManager.transfer(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, BigDecimal.valueOf(10L)));

        //THEN
        verify(accountRepository, times(2)).findById(anyLong());
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);

        assertEquals(BigDecimal.valueOf(120), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(30), toAccount.getBalance());
    }
}