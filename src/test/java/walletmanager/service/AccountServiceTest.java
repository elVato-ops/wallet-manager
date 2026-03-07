package walletmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.UserNotFoundException;
import walletmanager.repository.AccountRepository;
import walletmanager.repository.UserRepository;
import walletmanager.response.AccountResponse;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static walletmanager.utils.TestConstants.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest
{
    @InjectMocks
    private AccountService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Nested
    class ObtainAccountsForUser
    {
        @Test
        public void returnsAccounts_whenUserExists()
        {
            //GIVEN
            when(userRepository.existsById(USER_ID)).thenReturn(true);
            when(accountRepository.findByUserId(USER_ID)).thenReturn(List.of(account()));

            //WHEN
            List<AccountResponse> accounts = service.obtainAccountsForUser(USER_ID);

            //THEN
            verify(userRepository, times(1)).existsById(USER_ID);
            verifyNoMoreInteractions(userRepository);
            verify(accountRepository, times(1)).findByUserId(USER_ID);
            verifyNoMoreInteractions(accountRepository);

            assertEquals(BALANCE, accounts.get(0).balance());
            assertEquals(PLN, accounts.get(0).currency());
        }

        @Test
        public void throwsUserNotFoundException_whenUserNotExists()
        {
            //GIVEN
            when(userRepository.existsById(USER_ID)).thenReturn(false);

            //WHEN
            assertThrows(UserNotFoundException.class, () -> service.obtainAccountsForUser(USER_ID));

            //THEN
            verify(userRepository, times(1)).existsById(USER_ID);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(accountRepository);
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
            AccountResponse response = service.obtainAccount(ACCOUNT_ID);

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
            assertThrows(AccountNotFoundException.class, () -> service.obtainAccount(ACCOUNT_ID));

            //THEN
            verify(accountRepository, times(1)).findById(ACCOUNT_ID);
            verifyNoMoreInteractions(accountRepository);
        }
    }
}