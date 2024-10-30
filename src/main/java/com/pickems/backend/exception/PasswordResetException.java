package com.pickems.backend.exception;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;

public class PasswordResetException extends Throwable {

    public PasswordResetException(String failedToInitiatePasswordReset, AWSCognitoIdentityProviderException e) {
    }
}
