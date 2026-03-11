package walletmanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import walletmanager.repository.AccountRepository;
import walletmanager.repository.UserRepository;
import walletmanager.request.CreateAccountRequest;
import walletmanager.response.AccountResponse;
import walletmanager.response.UserResponse;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    public void setup()
    {
        userRepository.deleteAll();
    }

    @Test
    public void returns200_whenCreateUser_andFetchUser() throws Exception
    {
        //GIVEN
        String json = """
            {
              "name": "%s"
            }"""
                .formatted(USER_NAME);

        //WHEN
        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        Long userId = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class).id();

        mockMvc.perform(get("/users/" + userId)
                .param("page", "0")
                .param("size", "2"))

        //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.id").value(userId));

        assertTrue(userRepository.existsById(userId));
    }

    @Test
    public void returnsPaginatedUsers_whenCreateThreeUsers_andFetchAllUsers() throws Exception
    {
        //GIVEN
        String firstUserJson = """
            {
              "name": "%s"
            }"""
                .formatted(USER_NAME);

        String secondUserJson = """
            {
              "name": "%s"
            }"""
                .formatted(SECOND_USER_NAME);

        String thirdUserJson = """
            {
              "name": "%s"
            }"""
                .formatted(THIRD_USER_NAME);

        //WHEN
        MvcResult firstResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstUserJson))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult secondResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondUserJson))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult thirdResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(thirdUserJson))
                .andExpect(status().isCreated())
                .andReturn();

        //THEN
        Long firstUserId = objectMapper.readValue(firstResult.getResponse().getContentAsString(), UserResponse.class).id();
        Long secondUserId = objectMapper.readValue(secondResult.getResponse().getContentAsString(), UserResponse.class).id();
        Long thirdUserId = objectMapper.readValue(thirdResult.getResponse().getContentAsString(), UserResponse.class).id();

        assertTrue(userRepository.existsById(firstUserId));
        assertTrue(userRepository.existsById(secondUserId));
        assertTrue(userRepository.existsById(thirdUserId));

        //AND WHEN
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
    public void returns200_whenCreateAccountForUser_fetchAccount() throws Exception
    {
        //GIVEN
        String json = """
            {
              "name": "%s"
            }"""
                .formatted(USER_NAME);

        //WHEN
        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

        //THEN
                .andExpect(status().isCreated())
                .andReturn();

        Long userId = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class).id();
        assertTrue(userRepository.existsById(userId));

        //AND GIVEN
        CreateAccountRequest createAccountRequest =
                new CreateAccountRequest(PLN, BALANCE);

        //WHEN
        MvcResult createAccountResult = mockMvc.perform(post("/users/" + userId + "/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createAccountRequest)))

        //THEN
                .andExpect(status().isCreated())
                .andReturn();

        Long accountId = objectMapper.readValue(createAccountResult.getResponse().getContentAsString(), AccountResponse.class).id();
        assertTrue(accountRepository.existsById(accountId));

        //AND WHEN
        mockMvc.perform(get("/accounts/" + accountId))

        //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(toInt(accountId)))
                .andExpect(jsonPath("$.currency").value(PLN.toString()))
                .andExpect(jsonPath("$.balance").value(toInt(BALANCE)))
                .andExpect(jsonPath("$.userId").value(toInt(userId)));
    }
}