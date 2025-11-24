package com.cheburekmi.cheburek.integration;

import com.cheburekmi.cheburek.dto.LoginRequest;
import com.cheburekmi.cheburek.entity.User;
import com.cheburekmi.cheburek.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Value("${telegram.bot.token}")
    private String botToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldAuthenticateWithValidTelegramInitData() throws Exception {
        String telegramId = "123456789";
        long authDate = Instant.now().getEpochSecond();

        String userJson = String.format("{\"id\":%s,\"first_name\":\"Test\"}", telegramId);
        String dataCheckString = String.format("auth_date=%d\nuser=%s", authDate, userJson);
        String hash = calculateHash(dataCheckString, botToken);
        
        String fullInitData = String.format("auth_date=%d&user=%s&hash=%s", 
                authDate, 
                java.net.URLEncoder.encode(userJson, StandardCharsets.UTF_8), 
                hash);

        LoginRequest request = new LoginRequest();
        request.setInitData(fullInitData);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.telegramId").value(telegramId))
                .andExpect(jsonPath("$.user.userCode", notNullValue()));
    }

    @Test
    void shouldRejectInvalidHash() throws Exception {
        String telegramId = "123456789";
        long authDate = Instant.now().getEpochSecond();

        String userJson = String.format("{\"id\":%s}", telegramId);
        String initData = String.format(
                "auth_date=%d&user=%s&hash=invalid_hash",
                authDate, java.net.URLEncoder.encode(userJson, StandardCharsets.UTF_8)
        );

        LoginRequest request = new LoginRequest();
        request.setInitData(initData);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectExpiredAuthDate() throws Exception {
        String telegramId = "123456789";
        long authDate = Instant.now().getEpochSecond() - 400;

        String userJson = String.format("{\"id\":%s}", telegramId);
        String dataCheckString = String.format("auth_date=%d\nuser=%s", authDate, userJson);
        String hash = calculateHash(dataCheckString, botToken);
        
        String fullInitData = String.format("auth_date=%d&user=%s&hash=%s", 
                authDate, 
                java.net.URLEncoder.encode(userJson, StandardCharsets.UTF_8), 
                hash);

        LoginRequest request = new LoginRequest();
        request.setInitData(fullInitData);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateNewUserOnFirstLogin() throws Exception {
        String telegramId = "999888777";
        long authDate = Instant.now().getEpochSecond();

        String userJson = String.format("{\"id\":%s,\"first_name\":\"NewUser\"}", telegramId);
        String dataCheckString = String.format("auth_date=%d\nuser=%s", authDate, userJson);
        String hash = calculateHash(dataCheckString, botToken);
        
        String fullInitData = String.format("auth_date=%d&user=%s&hash=%s", 
                authDate, 
                java.net.URLEncoder.encode(userJson, StandardCharsets.UTF_8), 
                hash);

        LoginRequest request = new LoginRequest();
        request.setInitData(fullInitData);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));

        User user = userRepository.findByTelegramId(telegramId).orElseThrow();
        assertThat(user.getTelegramId()).isEqualTo(telegramId);
        assertThat(user.getUserCode()).isNotNull().hasSize(4);
    }

    private String calculateHash(String dataCheckString, String botToken) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] secretKey = digest.digest(botToken.getBytes(StandardCharsets.UTF_8));

        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
        hmac.init(secretKeySpec);

        byte[] hash = hmac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
}
