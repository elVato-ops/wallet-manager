package walletmanager.integration;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static walletmanager.utils.TestConstants.*;

public class AccountIntegrationTest extends BaseIntegrationTest
{
    @Nested
    class SuccessCases
    {
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
    }

    @Nested
    class FailureCases
    {

    }
}
