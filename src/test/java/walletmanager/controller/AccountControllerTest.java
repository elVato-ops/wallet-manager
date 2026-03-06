package walletmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.UserNotFoundException;
import walletmanager.request.CreateAccountRequest;
import walletmanager.response.AccountResponse;
import walletmanager.service.AccountService;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest
{
    private final AccountResponse response = new AccountResponse(997L, Currency.getInstance("PLN"), BigDecimal.valueOf(100L), 17L);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService service;

    @Nested
    class CreateAccount
    {
        @Test
        public void createAccount_returns201() throws Exception
        {
            CreateAccountRequest request =
                    new CreateAccountRequest(17L, Currency.getInstance("PLN"), BigDecimal.valueOf(100));

            when(service.createAccount(any(CreateAccountRequest.class))).thenReturn(response);

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.currency").value(response.currency().toString()))
                    .andExpect(jsonPath("$.balance").value(response.balance()))
                    .andExpect(jsonPath("$.userId").value(response.userId()));

            ArgumentCaptor<CreateAccountRequest> captor = ArgumentCaptor.forClass(CreateAccountRequest.class);
            verify(service).createAccount(captor.capture());

            CreateAccountRequest captorRequest = captor.getValue();
            assertEquals(17L, captorRequest.userId());
            assertEquals(Currency.getInstance("PLN"), captorRequest.currency());
            assertEquals(BigDecimal.valueOf(100L), captorRequest.balance());
        }

        @Test
        public void createAccount_badRequest_returns400() throws Exception
        {
            String json = """
        {
          "userId": 17,
          "balance": 100
        }""";

            verify(service, never()).createAccount(any());

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void createAccount_invalidUserId_returns400() throws Exception
        {
            String json = """
        {
          "userId": -3,
          "currency": "PLN",
          "balance": 100
        }""";

            verify(service, never()).createAccount(any());

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void createAccount_invalidBalance_returns400() throws Exception
        {
            String json = """
        {
          "userId": 3,
          "currency": "PLN",
          "balance": -100
        }""";

            verify(service, never()).createAccount(any());

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void createAccount_invalidCurrency_returns400() throws Exception
        {
            String json = """
        {
          "userId": "-3",
          "currency": null,
          "balance": "100"
        }""";

            verify(service, never()).createAccount(any());

            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetAccountForUser
    {
        @Test
        public void getAccountForUser_returnsAccount() throws Exception
        {
            when(service.obtainAccountsForUser(17L)).thenReturn(Set.of(response));

            mockMvc.perform(get("/accounts?userId=17")
                            .param("userId", "17"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(response.id()))
                    .andExpect(jsonPath("$[0].currency").value(response.currency().toString()))
                    .andExpect(jsonPath("$[0].balance").value(response.balance()))
                    .andExpect(jsonPath("$[0].userId").value(response.userId()));
        }

        @Test
        public void getAccountForUser_userNotExists_returns404() throws Exception
        {
            when(service.obtainAccountsForUser(17L)).thenThrow(new UserNotFoundException(17L));
            verify(service, never()).createAccount(any());

            mockMvc.perform(get("/accounts?userId=17"))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void getAccountForUser_invalidPath_returns400() throws Exception
        {
            verify(service, never()).createAccount(any());
            mockMvc.perform(get("/accounts?userId=abc"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetAccount
    {
        @Test
        public void getAccount_returnsAccount() throws Exception
        {
            when(service.obtainAccount(997L)).thenReturn(response);

            mockMvc.perform(get("/accounts/997"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.currency").value(response.currency().toString()))
                    .andExpect(jsonPath("$.balance").value(response.balance()))
                    .andExpect(jsonPath("$.userId").value(response.userId()));
        }

        @Test
        public void getAccount_notExists_returns404() throws Exception
        {
            when(service.obtainAccount(997L)).thenThrow(new AccountNotFoundException(997L));
            verify(service, never()).createAccount(any());

            mockMvc.perform(get("/accounts/997"))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void getAccount_invalidPath_returns400() throws Exception
        {
            verify(service, never()).createAccount(any());
            mockMvc.perform(get("/accounts/def"))
                    .andExpect(status().isBadRequest());
        }
    }
}
