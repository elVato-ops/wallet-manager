package walletmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import walletmanager.exception.UserNotFoundException;
import walletmanager.request.CreateAccountRequest;
import walletmanager.request.CreateUserRequest;
import walletmanager.response.AccountResponse;
import walletmanager.response.UserResponse;
import walletmanager.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static walletmanager.utils.TestConstants.*;

@WebMvcTest(UserController.class)
public class UserControllerTest
{
    @MockBean
    UserService service;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

            UserResponse response = new UserResponse(USER_ID, USER_NAME);
            when(service.createUser(any(CreateUserRequest.class))).thenReturn(response);

            ArgumentCaptor<CreateUserRequest> captor = ArgumentCaptor.forClass(CreateUserRequest.class);

            //WHEN
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/users/17"))
                    .andExpect(jsonPath("$.name").value(USER_NAME));

            verify(service).createUser(captor.capture());
            verifyNoMoreInteractions(service);

            CreateUserRequest captorRequest = captor.getValue();
            assertEquals(USER_NAME, captorRequest.name());
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
            UserResponse response = new UserResponse(ACCOUNT_ID, USER_NAME);

            when(service.getUser(ACCOUNT_ID)).thenReturn(response);

            //WHEN
            mockMvc.perform(get("/users/997"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(USER_NAME));

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

            verify(service, never()).getUser(anyLong());
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
            Page<UserResponse> userResponses = new PageImpl<>(List.of(userOne, userTwo));

            when(service.getAllUsers(any(Pageable.class))).thenReturn(userResponses);

            //WHEN
            mockMvc.perform(get("/users"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("Bobek", "Nubek")))
                    .andExpect(jsonPath("$.content[*].id", containsInAnyOrder(1, 2)));

            verify(service, times(1)).getAllUsers(any(Pageable.class));
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returnsPaginatedUsers() throws Exception
        {
            //GIVEN
            UserResponse userOne = new UserResponse(1L, "Bobek");
            UserResponse userTwo = new UserResponse(2L, "Nubek");

            Page<UserResponse> response =
                    new PageImpl<>(
                            List.of(userOne, userTwo),
                            PageRequest.of(0, 2),
                            5);

            when(service.getAllUsers(any(Pageable.class))).thenReturn(response);

            //WHEN
            mockMvc.perform(get("/users")
                    .param("page", "0")
                    .param("size", "2"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(2))
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.totalElements").value(5))
                    .andExpect(jsonPath("$.totalPages").value(3))

            //AND
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[*].name",
                            containsInAnyOrder("Bobek", "Nubek")))
                    .andExpect(jsonPath("$.content[*].id",
                            containsInAnyOrder(1, 2)));

            verify(service, times(1)).getAllUsers(any(Pageable.class));
            verifyNoMoreInteractions(service);
        }
    }

    @Nested
    class CreateAccount
    {
        @Test
        public void returns201_whenRequestValid() throws Exception
        {
            //GIVEN
            CreateAccountRequest request =
                    new CreateAccountRequest(PLN, BALANCE);

            AccountResponse response = new AccountResponse(ACCOUNT_ID, PLN, BALANCE, USER_ID);

            when(service.createAccount(any(CreateAccountRequest.class), eq(USER_ID))).thenReturn(response);
            ArgumentCaptor<CreateAccountRequest> captor = ArgumentCaptor.forClass(CreateAccountRequest.class);


            //WHEN
            mockMvc.perform(post("/users/" + USER_ID + "/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, "/users/" + USER_ID + "/accounts/997"))
                    .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                    .andExpect(jsonPath("$.currency").value(PLN.toString()))
                    .andExpect(jsonPath("$.balance").value(BALANCE))
                    .andExpect(jsonPath("$.userId").value(USER_ID));

            verify(service).createAccount(captor.capture(), eq(USER_ID));
            verifyNoMoreInteractions(service);

            CreateAccountRequest captorRequest = captor.getValue();
            assertEquals(PLN, captorRequest.currency());
            assertEquals(BALANCE, captorRequest.balance());
        }

        @Test
        public void returns400_whenRequestInvalid() throws Exception
        {
            //GIVEN
            String json = """
            {
              "balance": 100
            }""";

            //WHEN
            mockMvc.perform(post("/users/17/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).createAccount(any(), eq(17L));
        }

        @Test
        public void returns400_whenUserIdInvalid() throws Exception
        {
            //GIVEN
            String json = """
            {
              "currency": "PLN",
              "balance": 100
            }""";

            //WHEN
            mockMvc.perform(post("/users/-3/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).createAccount(any(), eq(-3L));
        }

        @Test
        public void returns400_whenBalanceNegative() throws Exception
        {
            //GIVEN
            String json = """
            {
              "currency": "PLN",
              "balance": -100
            }""";

            //WHEN
            mockMvc.perform(post("/users/3/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).createAccount(any(), eq(3L));
        }

        @Test
        public void returns400_whenCurrencyNull() throws Exception
        {
            //GIVEN
            String json = """
            {
              "currency": null,
              "amount": "100"
            }""";

            //WHEN
            mockMvc.perform(post("/users/3/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).createAccount(any(), eq(3L));
        }
    }
}