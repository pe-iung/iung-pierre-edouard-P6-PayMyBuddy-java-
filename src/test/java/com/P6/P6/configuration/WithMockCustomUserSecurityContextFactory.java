package com.P6.P6.configuration;

import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    private final UserEntityRepository userRepository;

    public WithMockCustomUserSecurityContextFactory(UserEntityRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        try {
            UserEntity user = userRepository.findByEmail(customUser.email())
                    .orElseThrow(() -> new RuntimeException(
                            "Test user not found with email: " + customUser.email()));

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            context.setAuthentication(auth);
            return context;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to create security context: " + e.getMessage(), e);
        }
    }
}
