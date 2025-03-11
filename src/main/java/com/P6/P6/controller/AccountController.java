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
            double balance = accountService.getBalance(user)/100;
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

            this(
                    transaction.getId(),
                    transaction.getSender().getId(),
                    Objects.equals(transaction.getReceiver().getId(), connectedUserId) ? transaction.getSender().getUsername() : transaction.getReceiver().getUsername(),
                    transaction.getDescription(),
                    Objects.equals(transaction.getReceiver().getId(), connectedUserId) ? (double) (transaction.getAmountInCents()-transaction.getFeeAmountInCents())/100 : (double) -1 * transaction.getAmountInCents()/100);
            log.info("receiver transaction amount is {}, sender amount is {}", (double) (transaction.getAmountInCents()-transaction.getFeeAmountInCents())/100,(double) -1 * transaction.getAmountInCents()/100 );

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
            double amountInCents = amount*100;
            accountService.transferMoney(senderId, receiverEmail, (int) amountInCents, description.trim());
            log.info("transfering money from senderId= {} to receiverEmail {} with amounnt in cents {}, and descripiotn = {} ", senderId, receiverEmail, amountInCents, description.trim());
            model.addAttribute("successMessage", "Transfer successful!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/account";
    }

    @PostMapping("/deposit")
    public String deposit(
            @RequestParam double amount,
            Model model
    ) {
        int amountInCents = (int) amount*100;

        if (amountInCents <= 0) {
            model.addAttribute("errorMessage",
                    "Deposit amount must be positive");
            return "redirect:/account";
        }
        try {


            UserEntity user = SecurityHelper.getConnectedUser();
            accountService.deposit(user, amountInCents);
            model.addAttribute("successMessage",
                    "Deposit successful!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/account";
    }


}
