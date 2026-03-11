package walletmanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import walletmanager.request.CreateAccountRequest;
import walletmanager.request.TransferRequest;
import walletmanager.response.AccountResponse;
import walletmanager.response.UserResponse;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static walletmanager.utils.TestConstants.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class WalletIntegrationTest
{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void returnsUser_whenCreateAndFetchUser() throws Exception
    {
        //GIVEN
        Long userId = createUser(USER_NAME);

        //WHEN
        mockMvc.perform(get("/users/" + userId)
                .param("page", "0")
                .param("size", "2"))

        //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    public void returnsPaginatedUsers_whenCreateAndFetchUsers() throws Exception
    {
        //GIVEN
        Long firstUserId = createUser(USER_NAME);
        Long secondUserId = createUser(SECOND_USER_NAME);
        Long thirdUserId = createUser(THIRD_USER_NAME);

        //WHEN
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "id"))

        //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", contains(USER_NAME, SECOND_USER_NAME)))
                .andExpect(jsonPath("$.content[*].id", contains(toInt(firstUserId), toInt(secondUserId))))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));

        //AND WHEN
        mockMvc.perform(get("/users")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "id"))

        //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value((THIRD_USER_NAME)))
                .andExpect(jsonPath("$.content[0].id").value(toInt(thirdUserId)))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));

        //AND WHEN
        mockMvc.perform(get("/users")
                        .param("page", "2")
                        .param("size", "2"))

        //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    public void returnsAccount_whenFetchAccountForUser() throws Exception
    {
        //GIVEN
        Long userId = createUser(USER_NAME);
        Long accountId = createAccount(userId, createAccountRequest());

        //WHEN
        getAccount(accountId)

        //THEN
                .andExpect(jsonPath("$.id").value(toInt(accountId)))
                .andExpect(jsonPath("$.currency").value(PLN.toString()))
                .andExpect(jsonPath("$.balance").value(toInt(BALANCE)))
                .andExpect(jsonPath("$.userId").value(toInt(userId)));
    }

    @Test
    public void createsTransactions_whenTransferBetweenAccounts() throws Exception
    {
        //GIVEN
        Long userId = createUser(USER_NAME);

        Long firstAccountId = createAccount(userId, createAccountRequest());
        Long secondAccountId = createAccount(userId, createOtherAccountRequest());

        transfer(firstAccountId, secondAccountId, BigDecimal.valueOf(20));
        transfer(secondAccountId, firstAccountId, BigDecimal.valueOf(50));

        //WHEN
        mockMvc.perform(get("/accounts/" + firstAccountId + "/transactions"))

        //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].fromAccountId", containsInAnyOrder(toInt(firstAccountId), toInt(secondAccountId))))
                .andExpect(jsonPath("$.content[*].toAccountId", containsInAnyOrder(toInt(firstAccountId), toInt(secondAccountId))))
                .andExpect(jsonPath("$.content[*].currency", containsInAnyOrder(PLN.toString(), PLN.toString())))
                .andExpect(jsonPath("$.content[*].amount", containsInAnyOrder(20,  50)));

        //AND WHEN
        mockMvc.perform(get("/accounts/" + secondAccountId + "/transactions"))

        //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].fromAccountId", containsInAnyOrder(toInt(firstAccountId), toInt(secondAccountId))))
                .andExpect(jsonPath("$.content[*].toAccountId", containsInAnyOrder(toInt(firstAccountId), toInt(secondAccountId))))
                .andExpect(jsonPath("$.content[*].currency", containsInAnyOrder(PLN.toString(), PLN.toString())))
                .andExpect(jsonPath("$.content[*].amount", containsInAnyOrder(20,  50)));
    }

    @Test
    public void updatesBalances_whenTransferBetweenAccounts() throws Exception
    {
        //GIVEN
        Long userId = createUser(USER_NAME);

        Long firstAccountId = createAccount(userId, createAccountRequest());
        Long secondAccountId = createAccount(userId, createOtherAccountRequest());

        transfer(firstAccountId, secondAccountId, BigDecimal.valueOf(20));
        transfer(secondAccountId, firstAccountId, BigDecimal.valueOf(50));

        //WHEN
        MvcResult firstAccountResult = getAccount(firstAccountId)
                .andReturn();

        MvcResult secondAccountResult = getAccount(secondAccountId)
                .andReturn();

        //THEN
        AccountResponse firstAccount = objectMapper.readValue(firstAccountResult.getResponse().getContentAsString(), AccountResponse.class);
        AccountResponse secondAccount = objectMapper.readValue(secondAccountResult.getResponse().getContentAsString(), AccountResponse.class);

        assertEquals(BigDecimal.valueOf(130), firstAccount.balance());
        assertEquals(BigDecimal.valueOf(170), secondAccount.balance());
    }

    private Long createUser(String userName) throws Exception
    {
        String json = """
            {
              "name": "%s"
            }"""
                .formatted(userName);

        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class).id();
    }

    private Long createAccount(Long userId, CreateAccountRequest request) throws Exception
    {
        MvcResult createAccountResult = mockMvc.perform(post("/users/" + userId + "/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(createAccountResult.getResponse().getContentAsString(), AccountResponse.class).id();
    }

    private void transfer(Long firstAccountId, Long secondAccountId, BigDecimal amount) throws Exception
    {
        TransferRequest request = new TransferRequest(firstAccountId, secondAccountId, amount);

        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated());
    }

    private ResultActions getAccount(Long id) throws Exception
    {
        return mockMvc.perform(get("/accounts/" + id + "/transactions"))
                .andExpect(status().isOk());
    }
}