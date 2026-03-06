package walletmanager.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import walletmanager.exception.UserNotFoundException;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.UserResponse;
import walletmanager.service.UserService;

import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest
{
    private static final Long USER_ID = 17L;
    private static final Long ACCOUNT_ID = 997L;
    
    @MockBean
    UserService service;

    @Autowired
    MockMvc mockMvc;

    @Nested
    class CreateUser
    {
        @Test
        public void returns201_whenRequestValid() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": "Bobek"
            }""";

            UserResponse response = new UserResponse(USER_ID, "Bobek");
            when(service.createUser(any(CreateUserRequest.class))).thenReturn(response);

            ArgumentCaptor<CreateUserRequest> captor = ArgumentCaptor.forClass(CreateUserRequest.class);

            //WHEN
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/users/17"))
                    .andExpect(jsonPath("$.name").value("Bobek"));

            verify(service).createUser(captor.capture());
            verify(service, times(1)).createUser(any());
            verifyNoMoreInteractions(service);

            CreateUserRequest captorRequest = captor.getValue();
            assertEquals("Bobek", captorRequest.name());
        }

        @Test
        public void returns400_whenNameEmpty() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": ""
            }""";

            //WHEN
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).createUser(any());
        }
    }

    @Nested
    class GetUser
    {
        @Test
        public void returns200_whenPathValid() throws Exception
        {
            //GIVEN
            UserResponse response = new UserResponse(ACCOUNT_ID, "Bobek");

            when(service.getUser(ACCOUNT_ID)).thenReturn(response);

            //WHEN
            mockMvc.perform(get("/users/997"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Bobek"));

            verify(service, times(1)).getUser(ACCOUNT_ID);
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returns404_whenUserNotExists() throws Exception
        {
            //GIVEN
            when(service.getUser(ACCOUNT_ID)).thenThrow(UserNotFoundException.class);

            //WHEN
            mockMvc.perform(get("/users/997"))

            //THEN
                    .andExpect(status().isNotFound());

            verify(service, times(1)).getUser(ACCOUNT_ID);
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returns400_whenPathInvalid() throws Exception
        {
            //GIVEN

            //WHEN
            mockMvc.perform(get("/users/abc"))

            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).getUser(any());
        }
    }

    @Nested
    class GetAllUsers
    {
        @Test
        public void returns200_whenRequestValid() throws Exception
        {
            //GIVEN
            UserResponse userOne = new UserResponse(1L, "Bobek");
            UserResponse userTwo = new UserResponse(2L, "Nubek");

            when(service.getAllUsers()).thenReturn(Set.of(userOne, userTwo));

            //WHEN
            mockMvc.perform(get("/users"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[*].name", containsInAnyOrder("Bobek", "Nubek")))
                    .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)));

            verify(service, times(1)).getAllUsers();
            verifyNoMoreInteractions(service);
        }
    }
}
