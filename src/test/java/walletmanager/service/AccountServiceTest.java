package walletmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.repository.AccountRepository;
import walletmanager.response.AccountResponse;
import walletmanager.utils.AccountMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static walletmanager.utils.TestConstants.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest
{
    @InjectMocks
    private AccountService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @Spy
    private AccountMapper accountMapper = new AccountMapper();

    @Nested
    class ObtainAccountsForUser
    {
        @Test
        public void returnsAccounts_whenUserExists()
        {
            //GIVEN
            when(accountRepository.findByUserId(USER_ID, PAGEABLE)).thenReturn(accountPageEntity());

            //WHEN
            Page<AccountResponse> accounts = service.getAccountsForUser(USER_ID, PAGEABLE);

            //THEN
            verify(accountRepository, times(1)).findByUserId(USER_ID, PAGEABLE);
            verifyNoMoreInteractions(accountRepository);

            assertEquals(BALANCE, accounts.stream().findFirst().get().balance());
            assertEquals(PLN, accounts.stream().findFirst().get().currency());
            assertEquals(1, accounts.getTotalElements());
            assertEquals(1, accounts.getTotalPages());
        }
    }

    @Nested
    class ObtainAccount
    {
        @Test
        public void returnsAccount_whenExists()
        {
            //GIVEN
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account()));

            //WHEN
            AccountResponse response = service.getAccount(ACCOUNT_ID);

            //THEN
            verify(accountRepository, times(1)).findById(ACCOUNT_ID);
            verifyNoMoreInteractions(accountRepository);

            assertEquals(PLN, response.currency());
            assertEquals(BALANCE, response.balance());
        }

        @Test
        public void throwsAccountNotFoundException_whenAccountNotExists()
        {
            //GIVEN
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

            //WHEN
            assertThrows(AccountNotFoundException.class, () -> service.getAccount(ACCOUNT_ID));

            //THEN
            verify(accountRepository, times(1)).findById(ACCOUNT_ID);
            verifyNoMoreInteractions(accountRepository);
        }
    }
}