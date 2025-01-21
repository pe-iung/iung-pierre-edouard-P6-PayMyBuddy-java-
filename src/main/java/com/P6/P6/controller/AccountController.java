package com.P6.P6.controller;

import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import com.P6.P6.service.AccountService;
import com.P6.P6.service.SecurityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final UserEntityRepository userEntityRepository;

    @GetMapping
    public String getAccountDetails(Model model) {
        UserEntity user = SecurityHelper.getConnectedUser();
        double balance = accountService.getBalance(user);
        model.addAttribute("balance", balance);
        return "account";
    }

    @PostMapping("/transfer")
    public String transferMoney(
            @RequestParam String receiverEmail,
            @RequestParam double amount,
            @RequestParam String description,
            Model model) {
        try {
            UserEntity sender = SecurityHelper.getConnectedUser();
            UserEntity receiver = userEntityRepository.findByEmail(receiverEmail)
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            accountService.transferMoney(sender, receiver, amount, description);
            model.addAttribute("successMessage", "Transfer successful!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/account";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam double amount, Model model) {
        try {
            UserEntity user = SecurityHelper.getConnectedUser();
            accountService.deposit(user, amount);
            model.addAttribute("successMessage", "Deposit successful!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/account";
    }
}
