package walletmanager.integration;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import walletmanager.request.CreateAccountRequest;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static walletmanager.utils.TestConstants.*;

public class UserIntegrationTest extends BaseIntegrationTest
{
    @Nested
    class UserCreation
    {
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
        public void returns400_whenCreateUser_withEmptyName() throws Exception
        {
            //WHEN
            postCreateUser("")

            //THEN
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void returns404_whenFetchNonExistUser() throws Exception
        {
            //WHEN
            mockMvc.perform(get("/users/" + NON_EXISTING_ID)
                            .param("page", "0")
                            .param("size", "2"))

            //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User " + NON_EXISTING_ID + " not found"))
                    .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        public void returns400_whenFetchUser_wrongIdFormat() throws Exception
        {
            //WHEN
            mockMvc.perform(get("/users/bob")
                            .param("page", "0")
                            .param("size", "2"))

            //THEN
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class AccountCreation
    {
        @Test
        public void returnsAccount_whenCreateAccountForExistingUser() throws Exception
        {
            //GIVEN
            Long userId = createUser(USER_NAME);

            //WHEN
            postCreateAccount(userId, createAccountRequest())

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.currency").value(PLN.toString()))
                    .andExpect(jsonPath("$.balance").value(toInt(BALANCE)))
                    .andExpect(jsonPath("$.userId").value(toInt(userId)));
        }

        @Test
        public void returns400_whenCreateAccount_requestInvalid() throws Exception
        {
            //GIVEN
            Long userId = createUser(USER_NAME);

            //WHEN
            postCreateAccount(userId, new CreateAccountRequest(PLN, BALANCE.negate()))

                    //THEN
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void returns404_whenCreateAccount_userNotExists() throws Exception
        {
            //WHEN
            postCreateAccount(NON_EXISTING_ID, createAccountRequest())

            //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User " +  NON_EXISTING_ID + " not found"))
                    .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }
}
