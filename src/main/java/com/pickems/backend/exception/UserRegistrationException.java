package com.pickems.backend.exception;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;

public class UserRegistrationException extends Throwable {

    String exceptionMessage;
    AWSCognitoIdentityProviderException exception;

    public UserRegistrationException(String exceptionMessage, AWSCognitoIdentityProviderException exception) {
        this.exceptionMessage = exceptionMessage;
        this.exception = exception;
    }
}
