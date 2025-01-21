package com.P6.P6.service;

import com.P6.P6.model.Account;
import com.P6.P6.model.Transaction;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.AccountRepository;
import com.P6.P6.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private static final double INITIAL_BALANCE = 0.0;

    @Transactional
    public Account createAccount(UserEntity user) {
        Account account = new Account(user, INITIAL_BALANCE);
        return accountRepository.save(account);
    }

    public double getBalance(UserEntity user) {
        return accountRepository.findByUser(user)
                .map(Account::getBalance)
                .orElseThrow(() -> new RuntimeException("Account not found for user: " + user.getUsername()));
    }

    @Transactional
    public void transferMoney(UserEntity sender, UserEntity receiver, double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account senderAccount = accountRepository.findByUser(sender)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        Account receiverAccount = accountRepository.findByUser(receiver)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (senderAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        // Update balances
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);

        // Save the accounts
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        // Create and save transaction record
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void deposit(UserEntity user, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
    }
}
