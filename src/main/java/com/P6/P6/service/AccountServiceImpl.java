package com.P6.P6.service;

import com.P6.P6.model.Account;
import com.P6.P6.model.Transaction;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.AccountRepository;
import com.P6.P6.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final UserService userService;

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private static final double INITIAL_BALANCE = 0.0;

    @Value("${transaction.fee.rate}")
    private int transactionFeeRate;

    /**
     * Bank account creation for a user
     * with can parametrize the initial balance
     * @param user
     * @return the saved entity in the account repository
     */
    @Override
    @Transactional
    public Account createAccount(UserEntity user) {

        log.info("from account service : this user was received {} with id {}",user,user.getId());

        Account account = new Account(user, INITIAL_BALANCE);
        return accountRepository.save(account);
    }

    /**
     * Bank account balance for a user
     * we store balance in cents in database but display it in euro on front-end
     * @param user
     * @return the balance in cents of euro (100cents = 1 euro)
     */
    @Override
    public double getBalance(UserEntity user) {
        return accountRepository.findByUser_Id(user.getId())
                .map(Account::getBalance)
                .orElseThrow(() -> new RuntimeException("Account not found for user: " + user.getUsername()));
    }

    /**
     * transfer money from one user to a friend previsouly added
     * we store balance in cents in database but display it in euro on front-end
     * we currently don't keep the fees in a company bank account, but this could be a future feature
     * @param senderId, receiverEmail, amountInCents, description
     * @return void
     */
    @Override
    @Transactional
    public void transferMoney(Integer senderId, String receiverEmail, int amountInCents, String description) {

        if (!StringUtils.hasText(description)) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        if (amountInCents <= 0) {
            throw new IllegalArgumentException("le montant transféré doit être positif");
        }

        UserEntity sender = userService.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        UserEntity receiver = userService.findByEmail(receiverEmail)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));


        if(senderId.equals(receiver.getId())){
            throw new IllegalArgumentException("Cannot transfer money to yourself");
        }


        if(!assertUsersAreFriends(sender, receiver)){
            throw new IllegalArgumentException("The receiver is not a friend");
        }

        Account senderAccount = accountRepository.findByUser_Id(sender.getId())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        if (senderAccount.getBalance() < amountInCents) {
            throw new RuntimeException("Fonds insuffisant");
        }



        int feeAmountInCents = (transactionFeeRate*amountInCents/100);
        log.info("calculated fee amount is {}",feeAmountInCents );
        //int amountInCents = (int) amount*100;

        Account receiverAccount = accountRepository.findByUser_Id(receiver.getId())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        // Update balances
        senderAccount.setBalance(senderAccount.getBalance() - amountInCents);
        receiverAccount.setBalance(receiverAccount.getBalance() +  (amountInCents - feeAmountInCents ));


        // Save the accounts
        accountRepository.saveAll(List.of(senderAccount, receiverAccount));

        // Create and save transaction record
        Transaction transaction = new Transaction(sender, receiver, feeAmountInCents, amountInCents, description);
        transactionRepository.save(transaction);


    }

    /**
     * assertUsersAreFriends
     * we check the receiver is a friend of the sender
     * @param sender, receiver
     * @return true if receiver is friend with sender
     */
    private static boolean assertUsersAreFriends(UserEntity sender, UserEntity receiver) {
        return sender.getFriends().stream()
                .map(UserEntity::getId)
                .anyMatch(id -> receiver.getId().equals(id));
    }

    /**
     * allow a user to deposit money on his bank account
     * @param user, amount
     * @return void
     */
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

    /**
     * allow a user to see his own bank account
     * @param user
     * @return account
     */
    private Account getAccount(UserEntity user) {
        return accountRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    /**
     * list all the transaction for a given user, either sent ones, or received ones
     * @param senderId, receiverId
     * @return list of transactions for a given user (both sent & received)
     */
    @Override
    public List<Transaction> getAllTransactionForUser(Integer senderId, Integer receiverId) {
        log.info("get All transaction for user senderId= {}, receiverId= {}", senderId, receiverId);
        return transactionRepository.findAllBySenderIdOrReceiverId(senderId,receiverId);
    }
}
