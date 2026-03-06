package walletmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        public void returns201_whenRequestValid() throws Exception
        {
            //GIVEN
            CreateAccountRequest request =
                    new CreateAccountRequest(17L, Currency.getInstance("PLN"), BigDecimal.valueOf(100));

            when(service.createAccount(any(CreateAccountRequest.class))).thenReturn(response);
            ArgumentCaptor<CreateAccountRequest> captor = ArgumentCaptor.forClass(CreateAccountRequest.class);

            //WHEN
            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, "/accounts/997"))
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.currency").value(response.currency().toString()))
                    .andExpect(jsonPath("$.balance").value(response.balance()))
                    .andExpect(jsonPath("$.userId").value(response.userId()));

            verify(service).createAccount(captor.capture());
            verify(service, times(1)).createAccount(any());
            verifyNoMoreInteractions(service);

            CreateAccountRequest captorRequest = captor.getValue();
            assertEquals(17L, captorRequest.userId());
            assertEquals(Currency.getInstance("PLN"), captorRequest.currency());
            assertEquals(BigDecimal.valueOf(100L), captorRequest.balance());
        }

        @Test
        public void returns400_whenRequestInvalid() throws Exception
        {
            //GIVEN
            String json = """
            {
              "userId": 17,
              "balance": 100
            }""";

            //WHEN
            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).createAccount(any());
        }

        @Test
        public void returns400_whenUserIdInvalid() throws Exception
        {
            //GIVEN
            String json = """
            {
              "userId": -3,
              "currency": "PLN",
              "balance": 100
            }""";

            verify(service, never()).createAccount(any());

            //WHEN
            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).createAccount(any());
        }

        @Test
        public void returns400_whenBalanceNegative() throws Exception
        {
            //GIVEN
            String json = """
            {
              "userId": 3,
              "currency": "PLN",
              "balance": -100
            }""";

            verify(service, never()).createAccount(any());

            //WHEN
            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).createAccount(any());
        }

        @Test
        public void returns400_whenCurrencyNull() throws Exception
        {
            //GIVEN
            String json = """
            {
              "userId": "-3",
              "currency": null,
              "balance": "100"
            }""";

            //WHEN
            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).createAccount(any());
        }
    }

    @Nested
    class GetAccountForUser
    {
        @Test
        public void returns201_whenRequestValid() throws Exception
        {
            //GIVEN
            when(service.obtainAccountsForUser(17L)).thenReturn(Set.of(response));

            //WHEN
            mockMvc.perform(get("/accounts")
                            .param("userId", "17"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(response.id()))
                    .andExpect(jsonPath("$[0].currency").value(response.currency().toString()))
                    .andExpect(jsonPath("$[0].balance").value(response.balance()))
                    .andExpect(jsonPath("$[0].userId").value(response.userId()));

            verify(service, times(1)).obtainAccountsForUser(17L);
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returns404_whenUserNotExists() throws Exception
        {
            //GIVEN
            when(service.obtainAccountsForUser(17L)).thenThrow(new UserNotFoundException(17L));

            //WHEN
            mockMvc.perform(get("/accounts")
                            .param("userId", "17"))

            //THEN
                    .andExpect(status().isNotFound());

            verify(service, times(1)).obtainAccountsForUser(17L);
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returns400_whenPathInvalid() throws Exception
        {
            //GIVEN

            //WHEN
            mockMvc.perform(get("/accounts")
                            .param("userId", "abc"))

            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).obtainAccountsForUser(any());
        }
    }

    @Nested
    class GetAccount
    {
        @Test
        public void returns200_whenUrlValid() throws Exception
        {
            //GIVEN
            when(service.obtainAccount(997L)).thenReturn(response);

            //WHEN
            mockMvc.perform(get("/accounts/997"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.currency").value(response.currency().toString()))
                    .andExpect(jsonPath("$.balance").value(response.balance()))
                    .andExpect(jsonPath("$.userId").value(response.userId()));

            verify(service, times(1)).obtainAccount(997L);
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returns404_whenAccountNotExists() throws Exception
        {
            //GIVEN
            when(service.obtainAccount(997L)).thenThrow(new AccountNotFoundException(997L));

            //WHEN
            mockMvc.perform(get("/accounts/997"))

            //THEN
                    .andExpect(status().isNotFound());

            verify(service, times(1)).obtainAccount(997L);
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returns400_whenPathInvalid() throws Exception
        {
            //GIVEN

            //WHEN
            mockMvc.perform(get("/accounts/def"))

            //THEN
                    .andExpect(status().isBadRequest());

            verify(service, never()).obtainAccount(any());
        }
    }
}
