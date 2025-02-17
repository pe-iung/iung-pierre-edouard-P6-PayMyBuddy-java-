package com.P6.P6.controller;

import ch.qos.logback.core.util.StringUtil;
import com.P6.P6.DTO.FriendListResponse;
import com.P6.P6.model.Transaction;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import com.P6.P6.service.AccountService;
import com.P6.P6.service.SecurityHelper;
import com.P6.P6.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    @GetMapping
    public String getAccountDetails(Model model) {
        try {
            UserEntity user = SecurityHelper.getConnectedUser();
            double balance = accountService.getBalance(user);
            List<Transaction> transactions = accountService.getAllTransactionForUser(user.getId(), user.getId());
            FriendListResponse friendList =
                    userService.getFriendList(SecurityHelper.getConnectedUser());
            model.addAttribute("friendList", friendList);
            model.addAttribute("balance", balance);
            model.addAttribute("transactions", transactions);
            return "account";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching account details");
            return "error";
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

//    @GetMapping("/transactions")
//    public String getTransactionHistory(Model model) {
//        try {
//            UserEntity user = SecurityHelper.getConnectedUser();
//            model.addAttribute("transactions",
//                    accountService.getUserTransactions(user));
//            return "transactions";
//        } catch (Exception e) {
//            model.addAttribute("errorMessage",
//                    "Error fetching transaction history");
//            return "error";
//        }
//    }
}
