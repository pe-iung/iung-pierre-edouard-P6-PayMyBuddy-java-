package com.P6.P6.service;

import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SignupService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserEntityRepository userEntityRepository;

    public void signupNewUser(SignupRequest signupRequest) {
        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserEntity newUser = new UserEntity(
                signupRequest.getUsername(),
                encodedPassword,
                signupRequest.getEmail()
        );

        userEntityRepository.save(newUser);
    }
}
