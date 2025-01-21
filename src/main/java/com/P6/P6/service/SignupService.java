package com.P6.P6.service;

import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.model.Account;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.AccountRepository;
import com.P6.P6.repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//todo add interface for injection dependance
@RequiredArgsConstructor
@Service
public class SignupService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserEntityRepository userEntityRepository;
    private final AccountRepository accountRepository;

    private static final double INITIAL_BALANCE = 0.0;

    @Transactional
    public UserEntity signupNewUser(SignupRequest signupRequest) {
        // Validate if email already exists
        if (userEntityRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create and save new user
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        UserEntity newUser = new UserEntity(
                signupRequest.getUsername(),
                encodedPassword,
                signupRequest.getEmail()
        );
        newUser = userEntityRepository.save(newUser);

        // Create and save associated account
        Account newAccount = new Account();
        newAccount.setUser(newUser);
        newAccount.setBalance(INITIAL_BALANCE);
        accountRepository.save(newAccount);

        return newUser;
    }
}

