package com.pickems.backend.exception;

import com.pickems.backend.model.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleUserRegistrationException(
    UserRegistrationException e) {
        return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(new ErrorResponse("Registration failed", e.getMessage()));
    }

    @ExceptionHandler(UserConfirmationException.class)
    public ResponseEntity<ErrorResponse> handleUserConfirmationException(
    UserConfirmationException e) {
        return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(new ErrorResponse("Confirmation failed", e.getMessage()));
    }
}