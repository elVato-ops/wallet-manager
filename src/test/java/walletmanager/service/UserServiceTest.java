package walletmanager.service;

import org.junit.jupiter.api.Nested;
import org.mockito.ArgumentCaptor;
import walletmanager.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import walletmanager.repository.UserRepository;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.UserResponse;
import walletmanager.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest
{
    private static final String USER_NAME = "Bobek";
    private static final Long USER_ID = 997L;

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Nested
    class CreateUser
    {
        @Test
        public void returnsUser_whenUserCreated()
        {
            //GIVEN
            UserEntity bobek = new UserEntity(USER_NAME);
            when(repository.save(any())).thenReturn(bobek);

            CreateUserRequest request = new CreateUserRequest(USER_NAME);
            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

            //WHEN
            UserResponse user = service.createUser(request);

            //THEN
            verify(repository).save(captor.capture());
            verifyNoMoreInteractions(repository);

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
            Optional<UserEntity> optionalUser = Optional.of(new UserEntity(USER_NAME));
            when(repository.findById(USER_ID)).thenReturn(optionalUser);

            //WHEN
            UserResponse user = service.getUser(USER_ID);

            //THEN
            assertEquals(USER_NAME, user.name());
            verify(repository, times(1)).findById(USER_ID);
            verifyNoMoreInteractions(repository);
        }

        @Test
        public void throwsUserNotFoundException_whenUserNotExists()
        {
            //GIVEN
            when(repository.findById(USER_ID)).thenReturn(Optional.empty());

            //WHEN
            assertThrows(UserNotFoundException.class, () -> service.getUser(USER_ID));

            //THEN
            verify(repository, times(1)).findById(USER_ID);
            verifyNoMoreInteractions(repository);
        }
    }

    @Nested
    class FindAllUsers
    {
        @Test
        public void returnsUsers()
        {
            //GIVEN
            List<UserEntity> users = List.of(new UserEntity(USER_NAME), new UserEntity("Chlebek"));
            when(repository.findAll()).thenReturn(users);

            //WHEN
            List<UserResponse> allUsers = service.getAllUsers();

            //THEN
            verify(repository, times(1)).findAll();
            verifyNoMoreInteractions(repository);

            assertEquals(List.of("Bobek", "Chlebek"),
                    allUsers.stream()
                            .map(UserResponse::name)
                            .collect(Collectors.toList()));
        }
    }
}