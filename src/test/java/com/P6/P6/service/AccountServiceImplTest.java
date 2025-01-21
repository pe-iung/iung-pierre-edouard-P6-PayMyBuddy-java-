package com.P6.P6.service;

import com.P6.P6.model.Account;
import com.P6.P6.model.Transaction;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.AccountRepository;
import com.P6.P6.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    private UserEntity testUser;
    private UserEntity friendUser;
    private Account testAccount;
    private Account friendAccount;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new UserEntity();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");

        // Setup friend user
        friendUser = new UserEntity();
        friendUser.setId(2);
        friendUser.setUsername("friend");
        friendUser.setEmail("friend@example.com");

        // Setup test account
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUser(testUser);
        testAccount.setBalance(1000.0);

        // Setup friend account
        friendAccount = new Account();
        friendAccount.setId(2L);
        friendAccount.setUser(friendUser);
        friendAccount.setBalance(500.0);
    }

    @Test
    void createAccount_Success() {
        // Arrange
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        Account result = accountServiceImpl.createAccount(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(1000.0, result.getBalance());
        verify(accountRepository).save(accountCaptor.capture());
        assertEquals(testUser, accountCaptor.getValue().getUser());
    }

    @Test
    void getBalance_Success() {
        // Arrange
        when(accountRepository.findByUser(testUser)).thenReturn(Optional.of(testAccount));

        // Act
        double balance = accountServiceImpl.getBalance(testUser);

        // Assert
        assertEquals(1000.0, balance);
        verify(accountRepository).findByUser(testUser);
    }

    @Test
    void getBalance_AccountNotFound() {
        // Arrange
        when(accountRepository.findByUser(testUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> accountServiceImpl.getBalance(testUser));
        verify(accountRepository).findByUser(testUser);
    }

    @Test
    void transferMoney_Success() {
        // Arrange
        when(accountRepository.findByUser(testUser)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByUser(friendUser)).thenReturn(Optional.of(friendAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        accountServiceImpl.transferMoney(testUser, friendUser, 500.0, "Test transfer");

        // Assert

        verify(accountRepository, times(2)).save(accountCaptor.capture());
        List<Account> savedAccounts = accountCaptor.getAllValues();
        assertEquals(500.0, savedAccounts.get(0).getBalance()); // Sender's new balance
        assertEquals(1000.0, savedAccounts.get(1).getBalance()); // Receiver's new balance
    }

    @Test
    void transferMoney_InsufficientFunds() {
        // Arrange
        when(accountRepository.findByUser(testUser)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByUser(friendUser)).thenReturn(Optional.of(friendAccount));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> accountServiceImpl.transferMoney(testUser, friendUser, 1500.0, "Test transfer"));

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferMoney_SenderAccountNotFound() {
        // Arrange
        when(accountRepository.findByUser(testUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> accountServiceImpl.transferMoney(testUser, friendUser, 500.0, "Test transfer"));

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferMoney_ReceiverAccountNotFound() {
        // Arrange
        when(accountRepository.findByUser(testUser)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByUser(friendUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> accountServiceImpl.transferMoney(testUser, friendUser, 500.0, "Test transfer"));

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deposit_Success() {
        // Arrange
        when(accountRepository.findByUser(testUser)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        accountServiceImpl.deposit(testUser, 500.0);

        // Assert
        verify(accountRepository).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();
        assertEquals(1500.0, savedAccount.getBalance());
    }

    @Test
    void deposit_NegativeAmount() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> accountServiceImpl.deposit(testUser, -100.0));

        verify(accountRepository, never()).save(any());
    }

    @Test
    void deposit_AccountNotFound() {
        // Arrange
        when(accountRepository.findByUser(testUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> accountServiceImpl.deposit(testUser, 500.0));

        verify(accountRepository, never()).save(any());
    }



    @Test
    void validateTransferAmount_InvalidAmount() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> accountServiceImpl.transferMoney(testUser, friendUser, -100.0, "Test"));
        assertThrows(IllegalArgumentException.class,
                () -> accountServiceImpl.transferMoney(testUser, friendUser, 0.0, "Test"));
    }

}
