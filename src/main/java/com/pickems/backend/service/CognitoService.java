package com.pickems.backend.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.pickems.backend.exception.PasswordResetException;
import com.pickems.backend.exception.UserConfirmationException;
import com.pickems.backend.exception.UserRegistrationException;
import com.pickems.backend.model.dto.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CognitoService {

    private final AWSCognitoIdentityProvider cognitoClient;

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.cognito.clientId}")
    private String clientId;

    public void registerUser(UserRegistrationRequest request) throws UserRegistrationException {
        try {
            SignUpRequest signUpRequest = new SignUpRequest()
                                          .withClientId(clientId)
                                          .withUsername(request.getEmail())
                                          .withPassword(request.getPassword())
                                          .withUserAttributes(
                                          new AttributeType()
                                          .withName("email")
                                          .withValue(request.getEmail()),
                                          new AttributeType()
                                          .withName("custom:role")
                                          .withValue("USER")
                                          );

            cognitoClient.signUp(signUpRequest);
            System.out.println();
        } catch (AWSCognitoIdentityProviderException e) {
            log.error("Error registering user", e);
            throw new UserRegistrationException("Failed to register user", e);
        }
    }

    public void confirmSignUp(String email, String confirmationCode) throws UserConfirmationException {
        try {
            ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest()
                                                        .withClientId(clientId)
                                                        .withUsername(email)
                                                        .withConfirmationCode(confirmationCode);

            cognitoClient.confirmSignUp(confirmSignUpRequest);

        } catch (AWSCognitoIdentityProviderException e) {
            log.error("Error confirming signup", e);
            throw new UserConfirmationException("Failed to confirm user", e);
        }
    }

    public void initiatePasswordReset(String email) throws PasswordResetException {
        try {
            ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest()
                                                          .withClientId(clientId)
                                                          .withUsername(email);

            cognitoClient.forgotPassword(forgotPasswordRequest);

        } catch (AWSCognitoIdentityProviderException e) {
            log.error("Error initiating password reset", e);
            throw new PasswordResetException("Failed to initiate password reset", e);
        }
    }
}