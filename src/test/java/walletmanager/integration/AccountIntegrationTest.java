package walletmanager.integration;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static walletmanager.utils.TestConstants.*;

public class AccountIntegrationTest extends BaseIntegrationTest
{
    @Nested
    class FetchAccount
    {
        @Test
        public void returnsAccount_whenExists() throws Exception
        {
            //GIVEN
            Long userId = createUser(USER_NAME);
            Long accountId = createAccount(userId, createAccountRequest());

            //WHEN
            getAccount(accountId)

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(toInt(accountId)))
                    .andExpect(jsonPath("$.currency").value(PLN.toString()))
                    .andExpect(jsonPath("$.balance").value(toInt(BALANCE)))
                    .andExpect(jsonPath("$.userId").value(toInt(userId)));
        }

        @Test
        public void returns400_whenIdFormatInvalid() throws Exception
        {
            //WHEN
            mockMvc.perform(get("/accounts/bobek"))

            //THEN
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void returns404_whenNotExists() throws Exception
        {
            //WHEN
            getAccount(NON_EXISTING_ID)

            //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Account " + NON_EXISTING_ID + " not found"))
                    .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    class FetchForUser
    {
        @Test
        public void returnsAccount_whenExists() throws Exception
        {
            //GIVEN
            Long userId = createUser(USER_NAME);
            Long accountId = createAccount(userId, createAccountRequest());

            //WHEN
            getAccountForUser(userId)

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[*].id").value(toInt(accountId)))
                    .andExpect(jsonPath("$.content[*].currency").value(PLN.toString()))
                    .andExpect(jsonPath("$.content[*].balance").value(toInt(BALANCE)))
                    .andExpect(jsonPath("$.content[*].userId").value(toInt(userId)));
        }

        @Test
        public void returns400_whenFormatInvalid() throws Exception
        {
            //WHEN
            getAccountForUser(-17L)

            //THEN
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void returns404_whenUserNotExists() throws Exception
        {
            //WHEN
            getAccountForUser(NON_EXISTING_ID)

            //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User " + NON_EXISTING_ID + " not found"))
                    .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    class FetchTransactions
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

            BigDecimal firstAmount =  BigDecimal.valueOf(20);
            BigDecimal secondAmount =  BigDecimal.valueOf(50);

            transfer(firstAccountId, secondAccountId, firstAmount);
            transfer(secondAccountId, firstAccountId, secondAmount);

            //WHEN
            getAccount(firstAccountId)

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(toInt(firstAccountId)))
                    .andExpect(jsonPath("$.currency").value(PLN.toString()))
                    .andExpect(jsonPath("$.balance").value(130))
                    .andExpect(jsonPath("$.userId").value(toInt(userId)));

            //AND WHEN
            getAccount(secondAccountId)

            //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(toInt(secondAccountId)))
                .andExpect(jsonPath("$.currency").value(PLN.toString()))
                .andExpect(jsonPath("$.balance").value(170))
                .andExpect(jsonPath("$.userId").value(toInt(userId)));
        }
    }
}