package com.pickems.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pickems.backend.model.dto.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Setter
@ToString
@RequiredArgsConstructor
@Builder
@Getter
@With
@AllArgsConstructor
@Entity(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String username;
    private String email;

    @JsonIgnore
    private Set<String> authorities;
    private String firstName;
    private String lastName;
    private String displayName;
    private String avatarUrl;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private Set<String> roles;
    private Set<String> permissions;

    public User(String username, String email, Set<String> authorities, String firstName, String lastName, String displayName, String avatarUrl, boolean emailVerified, LocalDateTime createdAt,
                LocalDateTime lastLoginAt, AccountStatus status, Set<String> roles, Set<String> permissions) {
        this.username = username;
        this.email = email;
        this.authorities = authorities;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
        this.status = status;
        this.roles = roles;
        this.permissions = permissions;
    }

    public boolean hasRole(String role) {
        return authorities.stream()
                          .map(SimpleGrantedAuthority::new)
                          .map(GrantedAuthority::getAuthority)
                          .anyMatch(authority -> authority.equals("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}