package walletmanager.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.UserNotFoundException;
import walletmanager.service.AccountService;

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
            when(service.getAccountsForUser(eq(USER_ID), any(Pageable.class))).thenReturn(accountPageResponse());

            //WHEN
            mockMvc.perform(get("/accounts")
                            .param("userId", "17"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[*].id").value(Integer.valueOf(ACCOUNT_ID.toString())))
                    .andExpect(jsonPath("$.content[*].currency").value(PLN.toString()))
                    .andExpect(jsonPath("$.content[*].balance").value(Integer.valueOf(BALANCE.toString())))
                    .andExpect(jsonPath("$.content[*].userId").value(Integer.valueOf(USER_ID.toString())));

            verify(service, times(1)).getAccountsForUser(eq(USER_ID), any(Pageable.class));
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returns404_whenUserNotExists() throws Exception
        {
            //GIVEN
            when(service.getAccountsForUser(eq(USER_ID), any(Pageable.class))).thenThrow(new UserNotFoundException(USER_ID));

            //WHEN
            mockMvc.perform(get("/accounts")
                            .param("userId", "17"))

            //THEN
                    .andExpect(status().isNotFound());

            verify(service, times(1)).getAccountsForUser(eq(USER_ID), any(Pageable.class));
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

            verify(service, never()).getAccountsForUser(anyLong(), eq(PAGEABLE));
        }
    }

    @Nested
    class GetAccount
    {
        @Test
        public void returns200_whenUrlValid() throws Exception
        {
            //GIVEN
            when(service.getAccount(ACCOUNT_ID)).thenReturn(accountResponse());

            //WHEN
            mockMvc.perform(get("/accounts/997"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                    .andExpect(jsonPath("$.currency").value(PLN.toString()))
                    .andExpect(jsonPath("$.balance").value(BALANCE))
                    .andExpect(jsonPath("$.userId").value(USER_ID));

            verify(service, times(1)).getAccount(ACCOUNT_ID);
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returns404_whenAccountNotExists() throws Exception
        {
            //GIVEN
            when(service.getAccount(ACCOUNT_ID)).thenThrow(new AccountNotFoundException(ACCOUNT_ID));

            //WHEN
            mockMvc.perform(get("/accounts/997"))

            //THEN
                    .andExpect(status().isNotFound());

            verify(service, times(1)).getAccount(ACCOUNT_ID);
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

            verify(service, never()).getAccount(anyLong());
        }
    }
}
