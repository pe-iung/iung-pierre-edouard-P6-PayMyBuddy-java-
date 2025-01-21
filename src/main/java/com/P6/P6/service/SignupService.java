package com.P6.P6.service;

import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.model.UserEntity;
import org.springframework.transaction.annotation.Transactional;

public interface SignupService {
    @Transactional
    UserEntity signupNewUser(SignupRequest signupRequest);
}
