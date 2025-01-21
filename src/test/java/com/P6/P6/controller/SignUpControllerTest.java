package com.P6.P6.controller;

import com.P6.P6.model.Account;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.AccountRepository;
import com.P6.P6.repositories.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserEntityRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void signupForm_ShouldReturnSignupPage() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("signupRequest"));
    }

    @Test
    @Transactional
    void handleSignup_ShouldCreateUserAndAccount() throws Exception {
        // Arrange
        String testUsername = "testUser";
        String testEmail = "test@example.com";
        String testPassword = "password123";

        // Act
        mockMvc.perform(post("/signup")
                        .param("username", testUsername)
                        .param("password", testPassword)
                        .param("email", testEmail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // Assert
        // Verify user was created
        UserEntity createdUser = userRepository.findByEmail(testEmail)
                .orElseThrow(() -> new AssertionError("User not found"));
        assertEquals(testUsername, createdUser.getUsername());
        assertEquals(testEmail, createdUser.getEmail());

        // Verify account was created
        Account userAccount = accountRepository.findByUser(createdUser)
                .orElseThrow(() -> new AssertionError("Account not found"));
        assertEquals(0.0, userAccount.getBalance());
        assertEquals(createdUser, userAccount.getUser());
    }

    @Test
    @Transactional
    void handleSignup_DuplicateEmail_ShouldFailAndNotCreateAccount() throws Exception {
        // Arrange - Create initial user
        mockMvc.perform(post("/signup")
                        .param("username", "firstUser")
                        .param("password", "password123")
                        .param("email", "duplicate@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // Act - Try to create second user with same email
        mockMvc.perform(post("/signup")
                        .param("username", "secondUser")
                        .param("password", "password123")
                        .param("email", "duplicate@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"));

        // Assert
        // Verify only one user and account exist for this email
        assertEquals(1, userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals("duplicate@example.com"))
                .count());

        UserEntity existingUser = userRepository.findByEmail("duplicate@example.com")
                .orElseThrow();
        assertEquals(1, accountRepository.findAll().stream()
                .filter(a -> a.getUser().equals(existingUser))
                .count());
    }

    @Test
    @Transactional
    void handleSignup_InvalidData_ShouldNotCreateUserOrAccount() throws Exception {
        // Act - Try to create user with invalid email
        mockMvc.perform(post("/signup")
                        .param("username", "testUser")
                        .param("password", "password123")
                        .param("email", "invalid-email"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"));

        // Assert
        assertTrue(userRepository.findByEmail("invalid-email").isEmpty());
        assertEquals(0, accountRepository.findAll().stream()
                .filter(a -> a.getUser().getEmail().equals("invalid-email"))
                .count());
    }

    @Test
    @Transactional
    void handleSignup_EmptyFields_ShouldNotCreateUserOrAccount() throws Exception {
        // Act
        mockMvc.perform(post("/signup")
                        .param("username", "")
                        .param("password", "")
                        .param("email", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"));

        // Assert
        assertEquals(0, userRepository.count());
        assertEquals(0, accountRepository.count());
    }
}
