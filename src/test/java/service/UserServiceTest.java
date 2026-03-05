package service;

import exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UserRepository;
import request.CreateUserRequest;
import response.UserResponse;
import user.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest
{
    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    public void createUser_returnsUser()
    {
        UserEntity bobek = new UserEntity("Bobek");
        when(repository.save(any())).thenReturn(bobek);

        CreateUserRequest request = new CreateUserRequest("Bobek");
        UserResponse user = service.createUser(request);
        assertEquals("Bobek", user.name());
    }

    @Test
    public void getUser_returnsUser()
    {
        Optional<UserEntity> optionalUser = Optional.of(new UserEntity("Bobek"));
        when(repository.findById(997L)).thenReturn(optionalUser);

        UserResponse user = service.getUser(997L);
        assertEquals("Bobek", user.name());
    }

    @Test
    public void getUser_notExists_throwsUserNotFoundException()
    {
        when(repository.findById(997L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getUser(997L));
    }

    @Test
    public void getAllUsers_returnsUsers()
    {
        List<UserEntity> users = List.of(new UserEntity("Bobek"), new UserEntity("Chlebek"));
        when(repository.findAll()).thenReturn(users);

        Set<UserResponse> allUsers = service.getAllUsers();
        assertEquals(2, allUsers.size());
    }
}
