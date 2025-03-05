package com.P6.P6.controller;

import com.P6.P6.DTO.FriendDTO;
import com.P6.P6.model.Transaction;
import com.P6.P6.model.UserEntity;
import com.P6.P6.service.AccountService;
import com.P6.P6.service.SecurityHelper;
import com.P6.P6.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    @Value("${transaction.fee.rate}")
    static private Double transactionFeeRate;

    @GetMapping
    public String getAccountDetails(Model model) {
        try {
            UserEntity user = SecurityHelper.getConnectedUser();
            double balance = accountService.getBalance(user);
            log.info("current user balance {}", balance);

            //List<Transaction> transactions = accountService.getAllTransactionForUser(user.getId(), user.getId());

            List<TransactionResponse> transactions = accountService.getAllTransactionForUser(user.getId(), user.getId())
                    .stream()
                    .map(t -> new TransactionResponse(t, user.getId()))
                    .toList();

            log.info("transactionResponse fetched are: {}", transactions);
            List<FriendDTO> friendList = userService.getFriendList(user)
                    .stream()
                    .map(FriendDTO::new)
                    .toList();
            model.addAttribute("friendList", friendList);

            model.addAttribute("balance", balance);

            model.addAttribute("transactions", transactions);
            return "home";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching account details");
            return "error";
        }
    }


    public record TransactionResponse(
            int id,
            int senderId,
            String receiverUsername,
            String description,
            double amount
    ) {
        public TransactionResponse(Transaction transaction, Integer connectedUserId) {

            // username = receiver username if transaction is sent
            // username = sender username if transaction is received
//            String transactionResponseUsername = "";
//            double transactionResponseAmount = 0.0;
//            if (Objects.equals(transaction.getReceiver().getId(), connectedUserId)) {
//                transactionResponseUsername = transaction.getSender().getUsername();
//
//            } else {
//                transactionResponseUsername = transaction.getReceiver().getUsername();
//


            // transactionResponseAmount = - transaction amount if transaction is sent
            // transactionResponseAmount =
            // ((1 - transactionFeeRate) * transaction.getAmount()) if transaction is received


            // Objects.equals(transaction.getReceiver().getId(), connectedUserId) ? transaction.getSender().getUsername() : transaction.getReceiver().getUsername(),


            this(
                    transaction.getId(),
                    transaction.getSender().getId(),
                    Objects.equals(transaction.getReceiver().getId(), connectedUserId) ? transaction.getSender().getUsername() : transaction.getReceiver().getUsername(),
                    //transaction.getReceiver().getUsername(),
                    transaction.getDescription(),
                    //transaction.getAmount());

                    Objects.equals(transaction.getReceiver().getId(), connectedUserId) ? (transaction.getAmount()-transaction.getFee()) : -1 * transaction.getAmount());
            log.info("receiver transaction amount is {}, sender amount is {}", (transaction.getAmount()-transaction.getFee()),-1 * transaction.getAmount() );

        }


    }


    @PostMapping("/transfer")
    public String transferMoney(
            @RequestParam String receiverEmail,
            @RequestParam double amount,
            @RequestParam String description,
            Model model
    ) {
        try {
            Integer senderId = SecurityHelper.getConnectedUser().getId();
            accountService.transferMoney(senderId, receiverEmail, amount, description.trim());
            model.addAttribute("successMessage", "Transfer successful!");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/account";
    }

    @PostMapping("/deposit")
    public String deposit(
            @RequestParam double amount,
            Model model
    ) {
        // Validate amount
        if (amount <= 0) {
            model.addAttribute("errorMessage",
                    "Deposit amount must be positive");
            return "redirect:/account";
        }
        try {


            UserEntity user = SecurityHelper.getConnectedUser();
            accountService.deposit(user, amount);
            model.addAttribute("successMessage",
                    "Deposit successful!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/account";
    }


}
