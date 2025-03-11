package com.P6.P6.controller;

import com.P6.P6.model.Account;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.AccountRepository;
import com.P6.P6.repositories.UserEntityRepository;
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
class AddFriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserEntityRepository userRepository;

    private UserEntity testUser;
    private UserEntity friendUser;
    private UserEntity friendUser2;
    private Account testAccount;
    private Account friendAccount;

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


            // Create friend user
            friendUser = new UserEntity();
            friendUser.setUsername("friend");
            friendUser.setEmail("friend@example.com");
            friendUser.setPassword("password");
            friendUser = userRepository.save(friendUser);

            // Create friend user
            friendUser2 = new UserEntity();
            friendUser2.setUsername("friend");
            friendUser2.setEmail("friengwdrsfd@example.com");
            friendUser2.setPassword("password");
            friendUser2.getFriends().add(testUser);
            friendUser2 = userRepository.save(friendUser2);

//            // Create test account
//            testAccount = new Account();
//            testAccount.setUser(testUser);
//            testAccount.setBalance(1000.0);
//            testAccount = accountRepository.save(testAccount);
//
//            // Create friend account
//            friendAccount = new Account();
//            friendAccount.setUser(friendUser);
//            friendAccount.setBalance(500.0);
//            friendAccount = accountRepository.save(friendAccount);

            // Flush and clear to ensure everything is saved
            userRepository.flush();
            accountRepository.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set up test data: " + e.getMessage(), e);
        }
    }
    @Test
    void showAddFriendForm_ShouldReturnAddFriendPage() throws Exception {
        mockMvc.perform(get("/add-friend")
                .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("add-friend"));
    }

    @Test
    @WithMockUser
    void addFriend_ShouldAddFriend() throws Exception {
        mockMvc.perform(post("/add-friend")
                        .with(user(testUser))
                        .param("friendEmail", "friend@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-friend"))
                .andExpect(model().attributeDoesNotExist("errorMessage"));
    }

    @Test
    @WithMockUser
    void addFriend_ShouldAddFriend_ThrowException() throws Exception {
        mockMvc.perform(post("/add-friend")
                        .with(user(friendUser2))
                        .param("friendEmail", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-friend"))
                .andExpect(model().attribute("errorMessage", "Friend is already added!"));
    }
    

    @Test
    @WithMockUser
    void addFriend_ShouldAddFriend_ThrowExceptionNotFound() throws Exception {
        mockMvc.perform(post("/add-friend")
                        .with(user(friendUser2))
                        .param("friendEmail", "test@examgukjygukjple.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-friend"))
                .andExpect(model().attribute("errorMessage", "Friend not found with email: test@examgukjygukjple.com"));
    }
}
