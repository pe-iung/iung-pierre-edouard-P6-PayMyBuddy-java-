package com.P6.P6.service;

import com.P6.P6.model.Account;
import com.P6.P6.model.Transaction;
import com.P6.P6.model.UserEntity;
import jakarta.transaction.Transactional;

import java.util.List;

public interface AccountService {
    @Transactional
    Account createAccount(UserEntity user);

    double getBalance(UserEntity user);

    @Transactional
    void transferMoney(Integer senderId, String receiver, double amount, String description);

    @Transactional
    void deposit(UserEntity user, double amount);

    List<Transaction> getAllTransactionForUser(Integer senderId, Integer receiverId);
}
