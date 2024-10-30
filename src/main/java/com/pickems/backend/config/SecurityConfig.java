package com.pickems.backend.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Value("${token.signing.key}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                   .cors()
                   .configurationSource(corsConfigurationSource())
                   .and()
                   .sessionManagement()
                   .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   .and()
                   .authorizeHttpRequests(request -> request.requestMatchers("/api/auth/**")
                                                            .permitAll()
                                                            .requestMatchers("/api/user/**")
                                                            .authenticated()
                                                            .requestMatchers("/api/health/**")
                                                            .permitAll()
                                                            .requestMatchers("/api/admin/**")
                                                            .hasRole("ADMIN")
                                                            .anyRequest()
                                                            .authenticated())
                   .oauth2ResourceServer()
                   .jwt()
                   .jwtAuthenticationConverter(jwtAuthenticationConverter)
                   .and()
                   .and()
                   .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Profile("test")
    public JwtDecoder testJwtDecoder() {
        // For testing, you can use a static public key or a simplified decoder
        return token -> {
            // This is a simplified test decoder
            // You can implement custom verification logic here
            return NimbusJwtDecoder.withSecretKey(getSecretKey())
                                   .build()
                                   .decode(token);
        };
    }

    private SecretKey getSecretKey() {
        final byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretBytes, "HMAC");
    }


    CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000",  // React development
        "https://your-production-domain.com"
        ));
        configuration.setAllowedMethods(Arrays.asList(
        "GET",
        "POST",
        "PUT",
        "DELETE",
        "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type"
        ));
        configuration.setExposedHeaders(List.of(
        "Authorization"
        ));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}