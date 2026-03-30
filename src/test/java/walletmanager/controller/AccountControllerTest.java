package walletmanager.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.response.TransactionResponse;
import walletmanager.service.AccountService;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
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

    @Nested
    class GetTransactions
    {
        @Test
        public void returns200_whenRequestValid() throws Exception
        {
            //GIVEN
            Page<TransactionResponse> response =
                    new PageImpl<>(
                            List.of(transactionResponse(), otherTransactionResponse()),
                            PageRequest.of(0, 2),
                            5);

            when(service.getTransactionsForAccount(eq(17L), any(Pageable.class))).thenReturn(response);

            //WHEN
            mockMvc.perform(get("/accounts/17/transactions")
                    .param("page", "0")
                    .param("size", "2"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(2))
                    .andExpect(jsonPath("$.totalElements").value(5))
                    .andExpect(jsonPath("$.totalPages").value(3))

                    //AND
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[*].id",
                            containsInAnyOrder(toInt(TRANSACTION_ID), toInt(OTHER_TRANSACTION_ID))))
                    .andExpect(jsonPath("$.content[*].fromAccountId",
                            containsInAnyOrder(toInt(FROM_ACCOUNT_ID), toInt(OTHER_FROM_ACCOUNT_ID))))
                    .andExpect(jsonPath("$.content[*].toAccountId",
                            containsInAnyOrder(toInt(TO_ACCOUNT_ID), toInt(OTHER_TO_ACCOUNT_ID))))
                    .andExpect(jsonPath("$.content[*].currency",
                            containsInAnyOrder(PLN.toString(), EUR.toString())))
                    .andExpect(jsonPath("$.content[*].amount",
                            containsInAnyOrder(toInt(TRANSFER_AMOUNT), toInt(OTHER_TRANSFER_AMOUNT))));

            verify(service, times(1)).getTransactionsForAccount(eq(17L), any(Pageable.class));
            verifyNoMoreInteractions(service);
        }

        @Test
        public void returns404_whenAccountNotExists() throws Exception
        {
            //GIVEN
            Page<TransactionResponse> response =
                    new PageImpl<>(
                            List.of(transactionResponse(), otherTransactionResponse()),
                            PageRequest.of(0, 2),
                            5);

            when(service.getTransactionsForAccount(eq(17L), any(Pageable.class))).thenThrow(new AccountNotFoundException(17L));

            //WHEN
            mockMvc.perform(get("/accounts/17/transactions")
                            .param("page", "0")
                            .param("size", "2"))

                    //THEN
                    .andExpect(status().isNotFound());

            verify(service, times(1)).getTransactionsForAccount(eq(17L), any(Pageable.class));
            verifyNoMoreInteractions(service);
        }
    }
}
