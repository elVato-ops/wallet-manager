package walletmanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class BaseIntegrationTest
{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    public Long createUser(String userName) throws Exception
    {
        MvcResult mvcResult = postCreateUser(userName)
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class).id();
    }

    public ResultActions postCreateUser(String userName) throws Exception
    {
        String json = """
            {
              "name": "%s"
            }"""
                .formatted(userName);

        return mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    public Long createAccount(Long userId, CreateAccountRequest request) throws Exception
    {
        MvcResult createAccountResult = postCreateAccount(userId, request)
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(createAccountResult.getResponse().getContentAsString(), AccountResponse.class).id();
    }

    public ResultActions postCreateAccount(Long userId, CreateAccountRequest request) throws Exception
    {
        return mockMvc.perform(post("/users/" + userId + "/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)));
    }

    public ResultActions transfer(Long firstAccountId, Long secondAccountId, BigDecimal amount) throws Exception
    {
        TransferRequest request = new TransferRequest(firstAccountId, secondAccountId, amount);

        return mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)));
    }

    public ResultActions getAccount(Long id) throws Exception
    {
        return mockMvc.perform(get("/accounts/" + id));
    }

    public ResultActions getAccountForUser(Long userId) throws Exception
    {
        return mockMvc.perform(get("/accounts")
                .param("userId", userId.toString()));
    }
}