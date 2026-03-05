package walletmanager.controller;

import walletmanager.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.UserResponse;
import walletmanager.service.UserService;

import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class UserControllerTest
{
    @MockBean
    UserService service;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void createUser_returns201() throws Exception
    {
        String json = """
        {
          "name": "Bobek"
        }""";

        UserResponse response = new UserResponse(17L, "Bobek");
        when(service.createUser(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/17"))
                .andExpect(jsonPath("$.name").value("Bobek"));
    }

    @Test
    public void getUser_returnsUser() throws Exception
    {
        UserResponse response = new UserResponse(997L, "Bobek");

        when(service.getUser(997L)).thenReturn(response);

        mockMvc.perform(get("/users/997"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bobek"));
    }

    @Test
    public void getUser_notExists_returns404() throws Exception
    {
        when(service.getUser(997L)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/users/997"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUsers_returnsUsers() throws Exception
    {
        UserResponse userOne = new UserResponse(1L, "Bobek");
        UserResponse userTwo = new UserResponse(2L, "Nubek");

        when(service.getAllUsers()).thenReturn(Set.of(userOne, userTwo));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Bobek", "Nubek")))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)));
    }
}
