package com.P6.P6.service;

import com.P6.P6.model.Account;
import com.P6.P6.model.Transaction;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.AccountRepository;
import com.P6.P6.repositories.TransactionRepository;
import com.P6.P6.repositories.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final UserService userService;

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private static final double INITIAL_BALANCE = 0.0;

    @Override
    @Transactional
    public Account createAccount(UserEntity user) {


        Account account = new Account(user, INITIAL_BALANCE);
        return accountRepository.save(account);
    }

    @Override
    public double getBalance(UserEntity user) {
        return accountRepository.findByUser_Id(user.getId())
                .map(Account::getBalance)
                .orElseThrow(() -> new RuntimeException("Account not found for user: " + user.getUsername()));
    }

    @Override
    @Transactional
    public void transferMoney(UserEntity sender, String receiverEmail, double amount, String description) {

        if (!StringUtils.hasText(description)) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        UserEntity receiver = userService.findByEmail(receiverEmail)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if(sender.getId().equals(receiver.getId())){
            throw new IllegalArgumentException("Cannot transfer money to yourself");
        }

        if(!assertUsersAreFriends(sender, receiver)){
            throw new IllegalArgumentException("The receiver is not a friend");
        }

        Account senderAccount = accountRepository.findByUser_Id(sender.getId())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        if (senderAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        Account receiverAccount = accountRepository.findByUser_Id(receiver.getId())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        // Update balances
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);

        // Save the accounts
        accountRepository.saveAll(List.of(senderAccount, receiverAccount));

        // Create and save transaction record
        Transaction transaction = new Transaction(sender, receiver, amount, description);
        transactionRepository.save(transaction);
    }

    private static boolean assertUsersAreFriends(UserEntity sender, UserEntity receiver) {
        return sender.getFriends().stream()
                .map(UserEntity::getId)
                .anyMatch(id -> receiver.getId().equals(id));
    }

    @Override
    @Transactional
    public void deposit(UserEntity user, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = getAccount(user);

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
    }

    private Account getAccount(UserEntity user) {
        return accountRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}
