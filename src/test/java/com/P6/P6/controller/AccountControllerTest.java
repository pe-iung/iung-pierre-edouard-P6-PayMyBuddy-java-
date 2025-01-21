package com.P6.P6.controller;

import com.P6.P6.configuration.TestConfig;
import com.P6.P6.configuration.WithMockCustomUser;
import com.P6.P6.model.Account;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.AccountRepository;
import com.P6.P6.repositories.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserEntityRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private UserEntity testUser;
    private UserEntity friendUser;
    private Account testAccount;
    private Account friendAccount;

    @BeforeEach
    @Transactional
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

            // Create test account
            testAccount = new Account();
            testAccount.setUser(testUser);
            testAccount.setBalance(1000.0);
            testAccount = accountRepository.save(testAccount);

            // Create friend account
            friendAccount = new Account();
            friendAccount.setUser(friendUser);
            friendAccount.setBalance(500.0);
            friendAccount = accountRepository.save(friendAccount);

            // Flush and clear to ensure everything is saved
            userRepository.flush();
            accountRepository.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set up test data: " + e.getMessage(), e);
        }
    }


    @Test
    @WithMockCustomUser(email = "test@example.com")
    @Transactional
    void deposit_Success() throws Exception {
        mockMvc.perform(post("/account/deposit")
                        .param("amount", "500.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify balance was updated
        Account updatedAccount = accountRepository.findByUser(testUser).orElseThrow();
        assertEquals(1500.0, updatedAccount.getBalance());
    }

    @Test
    @Transactional
    void transferMoney_Success() throws Exception {
        mockMvc.perform(post("/account/transfer")
                        .with(SecurityMockMvcRequestPostProcessors.authentication())
                        .param("receiverEmail", "friend@example.com")
                        .param("amount", "500.0")
                        .param("description", "Test transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify balances were updated
        Account updatedSenderAccount = accountRepository.findByUser(testUser).orElseThrow();
        Account updatedReceiverAccount = accountRepository.findByUser(friendUser).orElseThrow();
        assertEquals(500.0, updatedSenderAccount.getBalance());
        assertEquals(1000.0, updatedReceiverAccount.getBalance());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getAccountDetails_Success() throws Exception {
        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(view().name("account"))
                .andExpect(model().attributeExists("balance"))
                .andExpect(model().attribute("balance", 1000.0));
    }

    @Test
    void getAccountDetails_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/account"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }


    @Test
    @WithMockUser(username = "test@example.com")
    @Transactional
    void transferMoney_InsufficientFunds() throws Exception {
        mockMvc.perform(post("/account/transfer")
                        .param("receiverEmail", "friend@example.com")
                        .param("amount", "2000.0")
                        .param("description", "Test transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attributeExists("errorMessage"));

        // Verify balances remained unchanged
        Account senderAccount = accountRepository.findByUser(testUser).orElseThrow();
        Account receiverAccount = accountRepository.findByUser(friendUser).orElseThrow();
        assertEquals(1000.0, senderAccount.getBalance());
        assertEquals(500.0, receiverAccount.getBalance());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @Transactional
    void transferMoney_ReceiverNotFound() throws Exception {
        mockMvc.perform(post("/account/transfer")
                        .param("receiverEmail", "nonexistent@example.com")
                        .param("amount", "500.0")
                        .param("description", "Test transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attributeExists("errorMessage"));

        // Verify sender's balance remained unchanged
        Account senderAccount = accountRepository.findByUser(testUser).orElseThrow();
        assertEquals(1000.0, senderAccount.getBalance());
    }



    @Test
    @WithMockUser(username = "test@example.com")
    @Transactional
    void deposit_NegativeAmount() throws Exception {
        mockMvc.perform(post("/account/deposit")
                        .param("amount", "-500.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attributeExists("errorMessage"));

        // Verify balance remained unchanged
        Account account = accountRepository.findByUser(testUser).orElseThrow();
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @Transactional
    void transferMoney_InvalidAmount() throws Exception {
        mockMvc.perform(post("/account/transfer")
                        .param("receiverEmail", "friend@example.com")
                        .param("amount", "0.0")
                        .param("description", "Test transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attributeExists("errorMessage"));

        // Verify balances remained unchanged
        Account senderAccount = accountRepository.findByUser(testUser).orElseThrow();
        Account receiverAccount = accountRepository.findByUser(friendUser).orElseThrow();
        assertEquals(1000.0, senderAccount.getBalance());
        assertEquals(500.0, receiverAccount.getBalance());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @Transactional
    void transferMoney_ToSelf() throws Exception {
        mockMvc.perform(post("/account/transfer")
                        .param("receiverEmail", "test@example.com")
                        .param("amount", "500.0")
                        .param("description", "Test transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attributeExists("errorMessage"));

        // Verify balance remained unchanged
        Account account = accountRepository.findByUser(testUser).orElseThrow();
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getTransactionHistory_Success() throws Exception {
        mockMvc.perform(get("/account/transactions"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions"))
                .andExpect(model().attributeExists("transactions"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @Transactional
    void transferMoney_EmptyDescription() throws Exception {
        mockMvc.perform(post("/account/transfer")
                        .param("receiverEmail", "friend@example.com")
                        .param("amount", "500.0")
                        .param("description", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attributeExists("errorMessage"));

        // Verify balances remained unchanged
        Account senderAccount = accountRepository.findByUser(testUser).orElseThrow();
        Account receiverAccount = accountRepository.findByUser(friendUser).orElseThrow();
        assertEquals(1000.0, senderAccount.getBalance());
        assertEquals(500.0, receiverAccount.getBalance());
    }
}
