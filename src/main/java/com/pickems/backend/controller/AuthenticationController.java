package com.pickems.backend.controller;

import com.pickems.backend.exception.UserConfirmationException;
import com.pickems.backend.exception.UserRegistrationException;
import com.pickems.backend.model.dto.UserRegistrationRequest;
import com.pickems.backend.service.CognitoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final CognitoService cognitoService;

    @PostMapping("/register")
    //
    public ResponseEntity<Void> registerUser(@RequestBody @Valid UserRegistrationRequest request) throws UserRegistrationException {
        cognitoService.registerUser(request);
        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmSignUp(
    @RequestParam String email,
    @RequestParam String code) throws UserConfirmationException {
        cognitoService.confirmSignUp(email, code);
        return ResponseEntity.ok()
                             .build();
    }

}
