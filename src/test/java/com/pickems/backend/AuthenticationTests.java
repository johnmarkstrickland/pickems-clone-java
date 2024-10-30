package com.pickems.backend;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.pickems.backend.config.JwtAuthenticationConverter;
import com.pickems.backend.model.dto.UserRegistrationRequest;
import com.pickems.backend.service.CognitoService;
import com.pickems.backend.service.UserService;
import com.pickems.backend.utils.JwtTestUtils;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class AuthenticationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTestUtils jwtTestUtils;
    @MockBean
    private AWSCognitoIdentityProvider cognitoClient;

    @Autowired
    private CognitoService cognitoService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @BeforeEach
    public void setupTests() {
        final UserRegistrationRequest registrationRequest = UserRegistrationRequest.builder()
                                                                                   .email("firstuser@gmail.com")
                                                                                   .password("Password123")
                                                                                   .build();
        userService.registerUser(registrationRequest);
    }

    // Test user registration
    @Test
    public void testUserRegistration() throws Exception {
        // Given
        final UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
                                                                                       .email("test@example.com")
                                                                                       .password("Password123")
                                                                                       .build();

        SignUpResult signUpResult = new SignUpResult();
        when(cognitoClient.signUp(any(SignUpRequest.class)))
        .thenReturn(signUpResult);

        // When/Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\",\"password\":\"Password123!\"}"))
               .andExpect(status().isOk());

        verify(cognitoClient).signUp(any(SignUpRequest.class));
    }

    // Test registration validation
    @Test
    public void testRegistrationValidation() throws Exception {
        // Invalid email
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("{\"email\":\"invalid-email\",\"password\":\"Password123!\"}"))
               .andExpect(status().isBadRequest());

        // Invalid password
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\",\"password\":\"weak\"}"))
               .andExpect(status().isBadRequest());
    }

    // Test protected endpoints
    @Test
    public void testProtectedEndpoints() throws Exception {
        // Test with no authentication
        mockMvc.perform(get("/api/user/profile"))
               .andExpect(status().isUnauthorized());

        final String token = jwtTestUtils.generateToken("1", Map.of());
        // Test with user role
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        mockMvc.perform(get("/api/user/profile")
                        .headers(headers))
               .andExpect(status().isOk());

        // Test admin endpoint with user role
        mockMvc.perform(get("/api/admin")
                        .with(jwt().jwt(jwt -> jwt
                                               .claim("cognito:groups", Arrays.asList("USER")))))
               .andExpect(status().isForbidden());

        // Test admin endpoint with admin role
        //TODO implement rolebased
//        mockMvc.perform(get("/api/admin")
//                        .with(jwt().jwt(jwt -> jwt
//                                               .claim("cognito:groups", Arrays.asList("ADMIN")))))
//               .andExpect(status().isOk());
    }
}
