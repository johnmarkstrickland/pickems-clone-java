package com.pickems.backend.service;

import com.pickems.backend.model.dto.UserRegistrationRequest;
import com.pickems.backend.model.entity.User;
import com.pickems.backend.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    final UserRepository userRepository;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String name) {
        return userRepository.findByUsername(name);
    }

    public User registerUser(UserRegistrationRequest registrationRequest){
        final User build = User.builder()
                               .email(registrationRequest.getEmail())
                               .build();
        return userRepository.save(build);
    }

}
