package walletmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import walletmanager.entity.AccountEntity;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.UserNotFoundException;
import walletmanager.repository.AccountRepository;
import walletmanager.repository.UserRepository;
import walletmanager.request.CreateAccountRequest;
import walletmanager.response.AccountResponse;
import walletmanager.entity.UserEntity;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest
{
    private static final Long USER_ID = 997L;
    private static final UserEntity USER = new UserEntity("Bobek");
    private static final Long ACCOUNT_ID = 17L;
    private static final Currency PLN = Currency.getInstance("PLN");
    private static final BigDecimal BALANCE = BigDecimal.valueOf(100);
    private static final AccountEntity ACCOUNT = new AccountEntity(PLN, BALANCE, USER);

    @InjectMocks
    private AccountService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Nested
    class CreateAccount
    {
        @Test
        public void returnsAccount_whenAccountCreated()
        {
            //GIVEN
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));
            when(accountRepository.save(any())).thenReturn(ACCOUNT);

            CreateAccountRequest request = new CreateAccountRequest(USER_ID, PLN, BALANCE);
            ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);

            //WHEN
            AccountResponse account = service.createAccount(request);

            //THEN
            verify(userRepository, times(1)).findById(USER_ID);
            verifyNoMoreInteractions(userRepository);

            verify(accountRepository).save(captor.capture());
            verifyNoMoreInteractions(accountRepository);

            assertEquals(PLN, account.currency());
            assertEquals(BALANCE, account.balance());
        }

        @Test
        public void throwsUserNotFoundException_whenUserNotExists()
        {
            //GIVEN
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            CreateAccountRequest request = new CreateAccountRequest(USER_ID, PLN, BALANCE);

            //WHEN
            assertThrows(UserNotFoundException.class, () -> service.createAccount(request));

            //THEN
            verify(userRepository, times(1)).findById(USER_ID);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(accountRepository);
        }
    }

    @Nested
    class ObtainAccountsForUser
    {
        @Test
        public void returnsAccounts_whenUserExists()
        {
            //GIVEN
            when(userRepository.existsById(USER_ID)).thenReturn(true);
            when(accountRepository.findByUserId(USER_ID)).thenReturn(List.of(ACCOUNT));

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
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(ACCOUNT));

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