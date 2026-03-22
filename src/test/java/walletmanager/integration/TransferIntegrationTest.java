package walletmanager.integration;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import walletmanager.response.AccountResponse;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static walletmanager.utils.TestConstants.*;

public class TransferIntegrationTest extends BaseIntegrationTest
{
    @Nested
    class SuccessCases
    {
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
    }

    @Nested
    class FailureCases
    {

    }
}
