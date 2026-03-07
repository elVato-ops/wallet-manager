package walletmanager.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.UserNotFoundException;
import walletmanager.service.AccountService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static walletmanager.utils.TestConstants.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;

    @Nested
    class GetAccountForUser
    {
        @Test
        public void returns200_whenRequestValid() throws Exception
        {
            //GIVEN
            when(service.obtainAccountsForUser(USER_ID)).thenReturn(List.of(accountResponse()));

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
            when(service.obtainAccount(ACCOUNT_ID)).thenReturn(accountResponse());

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
