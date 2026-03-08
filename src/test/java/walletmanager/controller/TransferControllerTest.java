package walletmanager.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import walletmanager.exception.AccountNotFoundException;
import walletmanager.exception.IllegalTransactionException;
import walletmanager.request.TransferRequest;
import walletmanager.service.TransferService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static walletmanager.utils.TestConstants.*;

@WebMvcTest(TransferController.class)
public class TransferControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService service;

    @Test
    public void returns201_whenRequestValid() throws Exception
    {
        //GIVEN
        when(service.transfer(any(TransferRequest.class))).thenReturn(transactionResponse());

        ArgumentCaptor<TransferRequest> captor = ArgumentCaptor.forClass(TransferRequest.class);

        String json = """
            {
              "fromAccountId": 11,
              "toAccountId": 12,
              "amount": 20
            }""";

        //WHEN
        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

        //THEN
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/transfers/" + TRANSACTION_ID))
                .andExpect(jsonPath("$.id").value(TRANSACTION_ID))
                .andExpect(jsonPath("$.currency").value(PLN.toString()))
                .andExpect(jsonPath("$.amount").value(TRANSFER_AMOUNT))
                .andExpect(jsonPath("$.fromAccountId").value(FROM_ACCOUNT_ID))
                .andExpect(jsonPath("$.toAccountId").value(TO_ACCOUNT_ID));

        verify(service).transfer(captor.capture());
        verifyNoMoreInteractions(service);
    }

    @Test
    public void returns409_whenTransferToSameAccount() throws Exception
    {
        //GIVEN
        when(service.transfer(any(TransferRequest.class))).thenThrow(IllegalTransactionException.class);
        ArgumentCaptor<TransferRequest> captor = ArgumentCaptor.forClass(TransferRequest.class);

        String json = """
            {
              "fromAccountId": 12,
              "toAccountId": 12,
              "amount": 20
            }""";

        //WHEN
        mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

        //THEN
                .andExpect(status().isConflict());

        verify(service).transfer(captor.capture());
        verifyNoMoreInteractions(service);
    }

    @Test
    public void returns404_whenAccountNotExists() throws Exception
    {
        //GIVEN
        when(service.transfer(any(TransferRequest.class))).thenThrow(AccountNotFoundException.class);
        ArgumentCaptor<TransferRequest> captor = ArgumentCaptor.forClass(TransferRequest.class);

        String json = """
            {
              "fromAccountId": 999,
              "toAccountId": 12,
              "amount": 20
            }""";

        //WHEN
        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

        //THEN
                .andExpect(status().isNotFound());

        verify(service).transfer(captor.capture());
        verifyNoMoreInteractions(service);
    }

    @Test
    public void returns400_whenRequestInvalid() throws Exception
    {
        //GIVEN
        String json = """
            {
              "fromAccountId": 11,
              "toAccountId": 12,
              "amount": -20
            }""";

        //WHEN
        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

        //THEN
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}