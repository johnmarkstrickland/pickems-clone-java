package com.pickems.backend.config;

import com.pickems.backend.model.entity.User;
import com.pickems.backend.service.UserService;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    final static Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationConverter.class);
    final UserService userService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

        final String userId = jwt.getClaimAsString("sub");

        final Optional<User> byId = userService.findById(Long.valueOf(userId));
        if (byId.isEmpty()) {
            LOGGER.debug("No user found by id: " + userId);
            throw new AccessDeniedException("No user found by id");
        }

        final User user = User.builder()
                              .id(Long.valueOf(userId))
                              .email(jwt.getClaimAsString("email"))
                              .username(jwt.getClaimAsString("cognito:username"))
                              .build();

        return new UsernamePasswordAuthenticationToken(user, jwt.getTokenValue(), authorities);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<String> groups = jwt.getClaimAsStringList("cognito:groups");
        if (groups == null) {
            return Collections.emptyList();
        }

        return groups.stream()
                     .map(group -> new SimpleGrantedAuthority("ROLE_" + group.toUpperCase()))
                     .collect(Collectors.toList());
    }
}