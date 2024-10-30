package com.pickems.backend.model.dto;

import com.pickems.backend.ValidPassword;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserRegistrationRequest {
    @Email(message = "Email should be valid")
    String email;
    @ValidPassword
    String password;
}
