package com.P6.P6.controller;

import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import com.P6.P6.service.AccountService;
import com.P6.P6.service.SecurityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final UserEntityRepository userEntityRepository;

    @GetMapping
    public String getAccountDetails(Model model) {
        try {
            UserEntity user = SecurityHelper.getConnectedUser();
            double balance = accountService.getBalance(user);
            model.addAttribute("balance", balance);
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
            RedirectAttributes redirectAttributes
    ) {
        // Validate description
        if (description == null || description.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Description cannot be empty");
            return "redirect:/account";
        }

        try {
            UserEntity sender = SecurityHelper.getConnectedUser();
            UserEntity receiver = userEntityRepository.findByEmail(receiverEmail)
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            // Prevent self-transfer
            if (sender.getEmail().equals(receiverEmail)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Cannot transfer money to yourself");
                return "redirect:/account";
            }

            // Validate amount
            if (amount <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Transfer amount must be positive");
                return "redirect:/account";
            }

            accountService.transferMoney(sender, receiver, amount, description.trim());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Transfer successful!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/account";
    }

    @PostMapping("/deposit")
    public String deposit(
            @RequestParam double amount,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Validate amount
            if (amount <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Deposit amount must be positive");
                return "redirect:/account";
            }

            UserEntity user = SecurityHelper.getConnectedUser();
            accountService.deposit(user, amount);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Deposit successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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
