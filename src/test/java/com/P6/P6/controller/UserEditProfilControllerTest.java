package com.P6.P6.controller;

import com.P6.P6.model.Account;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.AccountRepository;
import com.P6.P6.repositories.UserEntityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserEditProfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserEntityRepository userRepository;

    private UserEntity testUser;


    @BeforeEach
    void setUp() {
        try {
            // Clean up existing test data
            accountRepository.deleteAll();
            userRepository.deleteAll();

            // Create test user
            testUser = new UserEntity();
            testUser.setUsername("testUser");
            testUser.setEmail("test@example.com");
            testUser.setPassword("password");
            testUser = userRepository.save(testUser);



            // Flush and clear to ensure everything is saved
            userRepository.flush();
            accountRepository.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set up test data: " + e.getMessage(), e);
        }
    }

    @BeforeEach
    @AfterEach
    void cleanDatabase(){
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    void showUserEditProfilForm_ShouldReturnEditProfilPage() throws Exception {
        mockMvc.perform(get("/user-edit-profil")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit-profil"));
    }

    @Test
    void editProfil_ShouldEditProfil() throws Exception {
        mockMvc.perform(post("/user-edit-profil")
                        .with(user(testUser))
                        .param("username", "userEdited")
                        .param("password", "password123Edited")
                        .param("email", "emailEdited@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-edit-profil"));
    }

    @Test
    void editProfilWithInvalidEmail_ShouldNotEditProfil() throws Exception {
        mockMvc.perform(post("/user-edit-profil")
                        .with(user(testUser))
                        .param("username", "userEdited")
                        .param("password", "password123Edited")
                        .param("email", "emailEdited"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-edit-profil"));
    }

}
