package com.P6.P6.service;

import com.P6.P6.model.Account;
import com.P6.P6.model.UserEntity;
import jakarta.transaction.Transactional;

public interface AccountService {
    @Transactional
    Account createAccount(UserEntity user);

    double getBalance(UserEntity user);

    @Transactional
    void transferMoney(UserEntity sender, UserEntity receiver, double amount, String description);

    @Transactional
    void deposit(UserEntity user, double amount);
}
