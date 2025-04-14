package com.auth.Controller;

import com.auth.Dtos.SignupRequest;
import com.auth.Models.user;
import com.auth.Repository.SessionRepository;
import com.auth.Repository.UserRepository;
import com.auth.Service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SessionRepository sessionRepository;

    private SignupRequest validSignupRequest;
    private user mockUser;

    @BeforeEach
    void setUp() {
        // Setup valid signup request
        validSignupRequest = new SignupRequest();
        validSignupRequest.setEmail("test@example.com");
        validSignupRequest.setPassword("password123");
        validSignupRequest.setRole("patient");
        validSignupRequest.setFullName("Test User");
        validSignupRequest.setPhoneNumber("1234567890");
        validSignupRequest.setGender("Male");

        // Setup mock user
        mockUser = new user();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("test@example.com");
        mockUser.setRole("patient");
        mockUser.setFullName("Test User");
    }

    @Test
    void register_ValidRequest_ReturnsSuccess() throws Exception {
        when(authService.registerUser(any(SignupRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    void register_InvalidRequest_ReturnsBadRequest() throws Exception {
        SignupRequest invalidRequest = new SignupRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword("short");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        when(authService.authenticate(any(String.class), any(String.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        when(authService.authenticate(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_ValidToken_ReturnsSuccess() throws Exception {
        String validToken = "valid-token";
        com.auth.Models.Session mockSession = Mockito.mock(com.auth.Models.Session.class);
        when(sessionRepository.findByAccessToken(validToken)).thenReturn(mockSession);

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    void logout_InvalidToken_ReturnsUnauthorized() throws Exception {
        String invalidToken = "invalid-token";
        when(sessionRepository.findByAccessToken(invalidToken)).thenReturn(null);

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }
} 