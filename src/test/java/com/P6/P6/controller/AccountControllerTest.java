package com.P6.P6.controller;

import com.P6.P6.model.Account;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.AccountRepository;
import com.P6.P6.repositories.UserEntityRepository;
import com.P6.P6.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {


    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserService addFriendServiceImpl;


    @Autowired
    private UserEntityRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private UserEntity testUser;
    private UserEntity friendUser;

    @BeforeEach
    @Transactional
    void setUp() {
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

            addFriendServiceImpl.addFriendToUserEntity(testUser.getEmail(),friendUser.getEmail());

            // Create test account
        Account testAccount = new Account();
            testAccount.setUser(testUser);
            testAccount.setBalance(100000.0);

            // Create friend account
        Account friendAccount = new Account();
            friendAccount.setUser(friendUser);
            friendAccount.setBalance(50000.0);
            accountRepository.saveAll(List.of(testAccount, friendAccount));
    }


    @BeforeEach
    @AfterEach
    void cleanDatabase(){
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }


    @WithMockUser("spring")
    @Test
    public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
        mvc.perform(get("/account").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser("spring")
    @Test
    @Transactional
    void deposit_Success() throws Exception {

        // Given
        UserEntity sender = createUser("sender@test.com", 1000.0);
        UserEntity receiver = createUser("receiver@test.com", 0.0);

        mvc.perform(post("/account/deposit")
                        .with(user(sender))
                        .param("amount", "500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));

        // Verify balance was updated
        Account updatedAccount = accountRepository.findByUser_Id(sender.getId()).orElseThrow();
        assertEquals(51000.0, updatedAccount.getBalance());
    }

    @Test
    @Transactional
    void transferMoney_Success() throws Exception {
        // Given
        UserEntity sender = createUser("sender@test.com", 100000.0);
        UserEntity receiver = createUser("receiver@test.com", 50000.0);
        addFriendServiceImpl.addFriendToUserEntity("sender@test.com", "receiver@test.com");

        mvc.perform(post("/account/transfer")
                        .with(user(sender))
                        .param("receiverEmail", "receiver@test.com")
                        .param("amount", "100.0")
                        .param("description", "Test transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));

        // Verify balances were updated
        Integer senderUserId = sender.getId();
        Integer receiverUserId = receiver.getId();

        Account updatedSenderAccount = accountRepository.findByUser_Id(senderUserId).orElseThrow();
        Account updatedReceiverAccount = accountRepository.findByUser_Id(receiverUserId).orElseThrow();
        assertEquals(90000.0, updatedSenderAccount.getBalance());
        assertEquals(59500.0, updatedReceiverAccount.getBalance());
    }

    @Test
    @WithMockUser("spring")
    void getAccountDetails_Success() throws Exception {
        mvc.perform(get("/account").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("balance"))
                .andExpect(model().attribute("balance", 1000.0));
    }

    @Test
    void getAccountDetails_Unauthenticated_RedirectsToLogin() throws Exception {
        mvc.perform(get("/account"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }


    @Test
    @Transactional
    void transferMoney_InsufficientFunds() throws Exception {
        mvc.perform(post("/account/transfer")
                        .with(user(testUser))
                        .param("receiverEmail", "friend@example.com")
                        .param("amount", "2000.0")
                        .param("description", "Test transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));


        // Verify balances remained unchanged
        Account senderAccount = accountRepository.findByUser_Id(testUser.getId()).orElseThrow();
        Account receiverAccount = accountRepository.findByUser_Id(friendUser.getId()).orElseThrow();
        assertEquals(100000.0, senderAccount.getBalance());
        assertEquals(50000.0, receiverAccount.getBalance());
    }

    @Test
    @Transactional
    void transferMoney_ReceiverNotFound() throws Exception {
        mvc.perform(post("/account/transfer")
                        .with(user(testUser))
                        .param("receiverEmail", "nonexistent@example.com")
                        .param("amount", "500.0")
                        .param("description", "Test transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));

        // Verify sender's balance remained unchanged
        Account senderAccount = accountRepository.findByUser_Id(testUser.getId()).orElseThrow();
        assertEquals(100000.0, senderAccount.getBalance());
    }


    @Test
    @Transactional
    void deposit_NegativeAmount() throws Exception {
        mvc.perform(post("/account/deposit")
                        .with(user(testUser))
                        .param("amount", "-500.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));

        // Verify balance remained unchanged
        Account account = accountRepository.findByUser_Id(testUser.getId()).orElseThrow();
        assertEquals(100000.0, account.getBalance());
    }

    @Test
    @Transactional
    void transferMoney_InvalidAmount() throws Exception {
        mvc.perform(post("/account/transfer")
                        .with(user(testUser))
                        .param("receiverEmail", "friend@example.com")
                        .param("amount", "0.0")
                        .param("description", "Test transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));

        // Verify balances remained unchanged
        Account senderAccount = accountRepository.findByUser_Id(testUser.getId()).orElseThrow();
        Account receiverAccount = accountRepository.findByUser_Id(friendUser.getId()).orElseThrow();
        assertEquals(100000.0, senderAccount.getBalance());
        assertEquals(50000.0, receiverAccount.getBalance());
    }

    @Test
    @Transactional
    void transferMoney_ToSelf() throws Exception {
        mvc.perform(post("/account/transfer")
                        .with(user(testUser))
                        .param("receiverEmail", "test@example.com")
                        .param("amount", "500.0")
                        .param("description", "Test transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));

        // Verify balance remained unchanged
        Account account = accountRepository.findByUser_Id(testUser.getId()).orElseThrow();
        assertEquals(100000.0, account.getBalance());
    }

    @Test
    @Transactional
    void transferMoney_EmptyDescription() throws Exception {
        mvc.perform(post("/account/transfer")
                        .with(user(testUser))
                        .param("receiverEmail", "friend@example.com")
                        .param("amount", "500.0")
                        .param("description", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));

        // Verify balances remained unchanged
        Account senderAccount = accountRepository.findByUser_Id(testUser.getId()).orElseThrow();
        Account receiverAccount = accountRepository.findByUser_Id(friendUser.getId()).orElseThrow();
        assertEquals(100000.0, senderAccount.getBalance());
        assertEquals(50000.0, receiverAccount.getBalance());
    }

    private UserEntity createUser(){
       return createUser("test@test.com", 1000.0);
    }

    private UserEntity createUser(String email, double initialBalance) {
        UserEntity user = new UserEntity();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword("password");
        user = userRepository.save(user);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(initialBalance);
        accountRepository.save(account);

        return user;
    }
}
