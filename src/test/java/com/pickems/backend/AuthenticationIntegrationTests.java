package com.pickems.backend;

import static com.pickems.backend.utils.TestUtils.OBJECT_MAPPER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.pickems.backend.model.dto.UserRegistrationRequest;
import com.pickems.backend.model.entity.User;
import com.pickems.backend.service.CognitoService;
import com.pickems.backend.service.UserService;
import com.pickems.backend.utils.JwtTestUtils;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthenticationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AWSCognitoIdentityProvider cognitoClient;
    @Autowired
    private JwtTestUtils jwtTestUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private CognitoService cognitoService;


    private User firstUser;

    @BeforeEach
    public void setupTests() {
        final UserRegistrationRequest registrationRequest = UserRegistrationRequest.builder()
                                                                                   .email("firstuser@gmail.com")
                                                                                   .password("Password123")
                                                                                   .build();
        firstUser = userService.registerUser(registrationRequest);
    }

    @Test
    public void testCompleteAuthFlow() throws Exception {
        // 1. Register User
        final UserRegistrationRequest registrationRequest = UserRegistrationRequest.builder()
                                                                                   .email("test@example.com")
                                                                                   .password("Password129!&")
                                                                                   .build();

        when(cognitoClient.signUp(any(SignUpRequest.class)))
        .thenReturn(new SignUpResult());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(OBJECT_MAPPER.writeValueAsString(registrationRequest)))
               .andExpect(status()
                          .is2xxSuccessful());

        // 2. Confirm Registration
        when(cognitoClient.confirmSignUp(any(ConfirmSignUpRequest.class)))
        .thenReturn(new ConfirmSignUpResult());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/confirm?email=test@example.com&code=123456"))
               .andExpect(status()
                          .is2xxSuccessful());

        // 3. Test Protected Endpoint Access
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtTestUtils.generateToken("1", Map.of()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/profile")
                                              .headers(headers))
               .andExpect(status()
                          .is2xxSuccessful());
    }

}
