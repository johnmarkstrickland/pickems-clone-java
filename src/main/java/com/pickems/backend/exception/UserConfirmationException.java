package com.pickems.backend.exception;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;

public class UserConfirmationException extends Throwable {

    public UserConfirmationException(String failedToConfirmUser, AWSCognitoIdentityProviderException e) {
    }
}
