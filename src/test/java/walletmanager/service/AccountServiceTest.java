package walletmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.UserNotFoundException;
import walletmanager.repository.AccountRepository;
import walletmanager.repository.UserRepository;
import walletmanager.response.AccountResponse;

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
    private UserRepository userRepository;

    @Nested
    class ObtainAccountsForUser
    {
        @Test
        public void returnsAccounts_whenUserExists()
        {
            //GIVEN
            when(userRepository.existsById(USER_ID)).thenReturn(true);
            when(accountRepository.findByUserId(USER_ID, PAGEABLE)).thenReturn(accountPageEntity());

            //WHEN
            Page<AccountResponse> accounts = service.getAccountsForUser(USER_ID, PAGEABLE);

            //THEN
            verify(userRepository, times(1)).existsById(USER_ID);
            verifyNoMoreInteractions(userRepository);
            verify(accountRepository, times(1)).findByUserId(USER_ID, PAGEABLE);
            verifyNoMoreInteractions(accountRepository);

            assertEquals(BALANCE, accounts.stream().findFirst().get().balance());
            assertEquals(PLN, accounts.stream().findFirst().get().currency());
            assertEquals(1, accounts.getTotalElements());
            assertEquals(1, accounts.getTotalPages());
        }

        @Test
        public void throwsUserNotFoundException_whenUserNotExists()
        {
            //GIVEN
            when(userRepository.existsById(USER_ID)).thenReturn(false);

            //WHEN
            assertThrows(UserNotFoundException.class, () -> service.getAccountsForUser(USER_ID, PAGEABLE));

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