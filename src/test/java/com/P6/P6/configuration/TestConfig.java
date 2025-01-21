package com.P6.P6.configuration;

import com.P6.P6.repositories.UserEntityRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public WithMockCustomUserSecurityContextFactory withMockCustomUserSecurityContextFactory(
            UserEntityRepository userRepository) {
        return new WithMockCustomUserSecurityContextFactory(userRepository);
    }
}