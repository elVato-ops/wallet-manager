package walletmanager.integration;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import walletmanager.request.CreateAccountRequest;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static walletmanager.utils.TestConstants.*;

public class TransferIntegrationTest extends BaseIntegrationTest
{
    @Nested
    class Transfer
    {
        @Test
        public void returnsTransaction_whenSuccess() throws Exception
        {
            //GIVEN
            Long userId = createUser(USER_NAME);

            Long firstAccountId = createAccount(userId, createAccountRequest());
            Long secondAccountId = createAccount(userId, createOtherAccountRequest());

            //WHEN
            transfer(firstAccountId, secondAccountId, TRANSFER_AMOUNT)

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.fromAccountId").value(toInt(firstAccountId)))
                    .andExpect(jsonPath("$.toAccountId").value(toInt(secondAccountId)))
                    .andExpect(jsonPath("$.currency").value(PLN.toString()))
                    .andExpect(jsonPath("$.amount").value(toInt(TRANSFER_AMOUNT)));
        }

        @Test
        public void returns400_whenAmountNegative() throws Exception
        {
            //GIVEN
            Long userId = createUser(USER_NAME);

            Long firstAccountId = createAccount(userId, createAccountRequest());
            Long secondAccountId = createAccount(userId, createOtherAccountRequest());

            //WHEN
            transfer(firstAccountId, secondAccountId, TRANSFER_AMOUNT.negate())

            //THEN
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void returns404_whenAccountNotExists() throws Exception
        {
            //GIVEN
            Long userId = createUser(USER_NAME);

            Long firstAccountId = createAccount(userId, createAccountRequest());

            //WHEN
            transfer(firstAccountId, NON_EXISTING_ID, TRANSFER_AMOUNT)

            //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Account " + NON_EXISTING_ID + " not found"))
                    .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        public void returns409_whenTransferToSameAccount() throws Exception
        {
            //GIVEN
            Long userId = createUser(USER_NAME);

            Long firstAccountId = createAccount(userId, createAccountRequest());

            //WHEN
            transfer(firstAccountId, firstAccountId, TRANSFER_AMOUNT)

            //THEN
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Cannot transfer to the same account"))
                    .andExpect(jsonPath("$.errorCode").value("ILLEGAL_TRANSACTION"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        public void returns409_whenDifferentCurrencies() throws Exception
        {
            //GIVEN
            Long userId = createUser(USER_NAME);

            Long firstAccountId = createAccount(userId, createAccountRequest());
            Long secondAccountId = createAccount(userId, new CreateAccountRequest(EUR, OTHER_BALANCE));

            //WHEN
            transfer(firstAccountId, secondAccountId, TRANSFER_AMOUNT)

                    //THEN
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Cannot transfer " + PLN + " to " + EUR))
                    .andExpect(jsonPath("$.errorCode").value("CURRENCY_MISMATCH"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        public void returns409_whenInsufficientFunds() throws Exception
        {
            //GIVEN
            Long userId = createUser(USER_NAME);

            Long firstAccountId = createAccount(userId, createAccountRequest());
            Long secondAccountId = createAccount(userId, createOtherAccountRequest());

            //WHEN
            transfer(firstAccountId, secondAccountId, new BigDecimal(1000))

            //THEN
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("You have insufficient funds to perform this operation"))
                    .andExpect(jsonPath("$.errorCode").value("INSUFFICIENT_FUNDS"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }
}
