package com.pickems.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pickems.backend.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    // Core identifiers
    private String id;              // Cognito sub
    private String email;
    private String username;

    // Profile information
    private String firstName;
    private String lastName;
    private String displayName;     // Custom display name if set
    private String avatarUrl;       // Profile picture URL

    // Account status
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private AccountStatus status;   // ACTIVE, SUSPENDED, etc.

    // Roles and Permissions
    private Set<String> roles;      // ADMIN, USER, etc.
    private Set<String> permissions;

    // Statistics
//    @Builder.Default
//    private UserStats stats = new UserStats();
//
//    // Preferences
//    @Builder.Default
//    private UserPreferences preferences = new UserPreferences();

    // Helper method to create from User entity
    public static UserResponse from(User user) {
        return UserResponse.builder()
                           .id(String.valueOf(user.getId()))
                           .email(user.getEmail())
                           .username(user.getUsername())
                           .firstName(user.getFirstName())
                           .lastName(user.getLastName())
                           .displayName(user.getDisplayName())
                           .avatarUrl(user.getAvatarUrl())
                           .emailVerified(user.isEmailVerified())
                           .createdAt(user.getCreatedAt())
                           .lastLoginAt(user.getLastLoginAt())
                           .status(user.getStatus())
                           .roles(user.getRoles())
                           .permissions(user.getPermissions())
                           .build();
    }
}