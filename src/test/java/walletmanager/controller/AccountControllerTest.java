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
    private static final Long USER_ID = 17L;
    private static final Long ACCOUNT_ID = 997L;
    private static final Currency PLN = Currency.getInstance("PLN");
    private static final BigDecimal BALANCE = BigDecimal.valueOf(100L);

    private final AccountResponse response = new AccountResponse(ACCOUNT_ID, PLN, BALANCE, USER_ID);

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
                    new CreateAccountRequest(USER_ID, PLN, BigDecimal.valueOf(100));

            when(service.createAccount(any(CreateAccountRequest.class))).thenReturn(response);
            ArgumentCaptor<CreateAccountRequest> captor = ArgumentCaptor.forClass(CreateAccountRequest.class);

            //WHEN
            mockMvc.perform(post("/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request)))

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, "/accounts/997"))
                    .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                    .andExpect(jsonPath("$.currency").value(PLN.toString()))
                    .andExpect(jsonPath("$.balance").value(BALANCE))
                    .andExpect(jsonPath("$.userId").value(USER_ID));

            verify(service).createAccount(captor.capture());
            verifyNoMoreInteractions(service);

            CreateAccountRequest captorRequest = captor.getValue();
            assertEquals(USER_ID, captorRequest.userId());
            assertEquals(PLN, captorRequest.currency());
            assertEquals(BALANCE, captorRequest.balance());
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
        public void returns200_whenRequestValid() throws Exception
        {
            //GIVEN
            when(service.obtainAccountsForUser(USER_ID)).thenReturn(Set.of(response));

            //WHEN
            mockMvc.perform(get("/accounts")
                            .param("userId", "17"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(ACCOUNT_ID))
                    .andExpect(jsonPath("$[0].currency").value(PLN.toString()))
                    .andExpect(jsonPath("$[0].balance").value(BALANCE))
                    .andExpect(jsonPath("$[0].userId").value(USER_ID));

            verify(service, times(1)).obtainAccountsForUser(USER_ID);
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returns404_whenUserNotExists() throws Exception
        {
            //GIVEN
            when(service.obtainAccountsForUser(USER_ID)).thenThrow(new UserNotFoundException(USER_ID));

            //WHEN
            mockMvc.perform(get("/accounts")
                            .param("userId", "17"))

            //THEN
                    .andExpect(status().isNotFound());

            verify(service, times(1)).obtainAccountsForUser(USER_ID);
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

            verify(service, never()).obtainAccountsForUser(anyLong());
        }
    }

    @Nested
    class GetAccount
    {
        @Test
        public void returns200_whenUrlValid() throws Exception
        {
            //GIVEN
            when(service.obtainAccount(ACCOUNT_ID)).thenReturn(response);

            //WHEN
            mockMvc.perform(get("/accounts/997"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                    .andExpect(jsonPath("$.currency").value(PLN.toString()))
                    .andExpect(jsonPath("$.balance").value(BALANCE))
                    .andExpect(jsonPath("$.userId").value(USER_ID));

            verify(service, times(1)).obtainAccount(ACCOUNT_ID);
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returns404_whenAccountNotExists() throws Exception
        {
            //GIVEN
            when(service.obtainAccount(ACCOUNT_ID)).thenThrow(new AccountNotFoundException(ACCOUNT_ID));

            //WHEN
            mockMvc.perform(get("/accounts/997"))

            //THEN
                    .andExpect(status().isNotFound());

            verify(service, times(1)).obtainAccount(ACCOUNT_ID);
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

            verify(service, never()).obtainAccount(anyLong());
        }
    }
}
