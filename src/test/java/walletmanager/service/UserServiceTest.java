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
import org.springframework.data.domain.PageImpl;
import walletmanager.entity.User;
import walletmanager.exception.UserNotFoundException;
import walletmanager.repository.UserRepository;
import walletmanager.request.CreateAccountRequest;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.AccountResponse;
import walletmanager.response.UserResponse;
import walletmanager.utils.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static walletmanager.utils.TestConstants.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest
{
    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountService accountService;

    @Spy
    private UserMapper userMapper = new UserMapper();

    @Nested
    class CreateUser
    {
        @Test
        public void returnsUser_whenUserCreated()
        {
            //GIVEN
            when(userRepository.save(any())).thenReturn(user());

            CreateUserRequest request = new CreateUserRequest(USER_NAME);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            //WHEN
            UserResponse user = service.createUser(request);

            //THEN
            verify(userRepository).save(captor.capture());
            verifyNoMoreInteractions(userRepository);

            assertEquals(USER_NAME, user.name());
            assertEquals(USER_NAME, captor.getValue().getName());
        }
    }

    @Nested
    class FindUserById
    {
        @Test
        public void returnsUser_whenUserExists()
        {
            //GIVEN
            Optional<User> optionalUser = Optional.of(user());
            when(userRepository.findById(USER_ID)).thenReturn(optionalUser);

            //WHEN
            UserResponse user = service.getUser(USER_ID);

            //THEN
            assertEquals(USER_NAME, user.name());
            verify(userRepository, times(1)).findById(USER_ID);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        public void throwsUserNotFoundException_whenUserNotExists()
        {
            //GIVEN
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            //WHEN
            assertThrows(UserNotFoundException.class, () -> service.getUser(USER_ID));

            //THEN
            verify(userRepository, times(1)).findById(USER_ID);
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    class FindAllUsers
    {
        @Test
        public void returnsUsers()
        {
            //GIVEN
            Page<User> userEntities = new PageImpl<>(List.of(user(), otherUser()));
            when(userRepository.findAll(PAGEABLE)).thenReturn(userEntities);

            //WHEN
            Page<UserResponse> allUsers = service.getAllUsers(PAGEABLE);

            //THEN
            verify(userRepository, times(1)).findAll(PAGEABLE);
            verifyNoMoreInteractions(userRepository);

            assertEquals(List.of(USER_NAME, SECOND_USER_NAME),
                    allUsers.stream()
                            .map(UserResponse::name)
                            .toList());
        }
    }
    @Nested
    class CreateAccount
    {
        @Test
        public void returnsAccount_whenAccountCreated()
        {
            //GIVEN
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));
            when(accountService.createAccount(any(CreateAccountRequest.class), any(User.class))).thenReturn(accountResponse());

            CreateAccountRequest request = new CreateAccountRequest(PLN, BALANCE);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            //WHEN
            AccountResponse account = service.createAccount(request, USER_ID);

            //THEN
            verify(userRepository, times(1)).findById(USER_ID);
            verifyNoMoreInteractions(userRepository);

            verify(accountService).createAccount(eq(request), captor.capture());
            verifyNoMoreInteractions(accountService);

            assertEquals(PLN, account.currency());
            assertEquals(BALANCE, account.balance());
        }

        @Test
        public void throwsUserNotFoundException_whenUserNotExists()
        {
            //GIVEN
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            CreateAccountRequest request = new CreateAccountRequest(PLN, BALANCE);

            //WHEN
            assertThrows(UserNotFoundException.class, () -> service.createAccount(request, USER_ID));

            //THEN
            verify(userRepository, times(1)).findById(USER_ID);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(accountService);
        }
    }

    @Nested
    class GetAccounts
    {
        @Test
        public void returnsAccounts_whenUserExists()
        {
            //GIVEN
            when(userRepository.existsById(USER_ID)).thenReturn(true);
            when(accountService.getAccountsForUser(USER_ID, PAGEABLE)).thenReturn(accountPageResponse());

            //WHEN
            Page<AccountResponse> accounts = service.getAccountsForUser(USER_ID, PAGEABLE);

            //THEN
            verify(userRepository, times(1)).existsById(USER_ID);
            verifyNoMoreInteractions(userRepository);
            verify(accountService, times(1)).getAccountsForUser(USER_ID, PAGEABLE);
            verifyNoMoreInteractions(accountService);

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
            verifyNoInteractions(accountService);
        }
    }
}