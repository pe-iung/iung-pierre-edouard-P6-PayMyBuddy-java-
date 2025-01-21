package com.P6.P6.service;

import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SignupServiceTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserEntityRepository userEntityRepository;

    @InjectMocks
    private SignupService signupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signupNewUser_ShouldCreateNewUser() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setUsername("testUser");
        request.setPassword("password123");
        request.setEmail("test@example.com");

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // Act
        signupService.signupNewUser(request);

        // Assert
        verify(passwordEncoder).encode("password123");
        verify(userEntityRepository).save(any(UserEntity.class));
    }
}
