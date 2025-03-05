package com.P6.P6.controller;

import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.service.AccountServiceImpl;
import com.P6.P6.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SignUpController {

    private final UserService signupService;
    private final AccountServiceImpl accountServiceImpl;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        if (!model.containsAttribute("signupRequest")) {
            model.addAttribute("signupRequest", new SignupRequest());
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String handleSignup(
            @Valid @ModelAttribute("signupRequest") SignupRequest signupRequest,
            BindingResult bindingResult,
            Errors errors,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        log.debug("Validation errors: {}", bindingResult.getAllErrors());

        if (errors.hasErrors()) {
            List<String> errorMessages = errors.getAllErrors().stream().map(ObjectError::toString).toList();

            model.addAttribute("errorMessages" , errorMessages);
            return "redirect:/signup";
        }

        try {
            Integer newUserId= signupService.signupNewUser(signupRequest);
            model.addAttribute(
                    "successMessage",
                    "Signup successful! Please login."
            );
            log.info("the user signupt request {} has been signUp with id {}",signupRequest, newUserId );
            signupService.findById(newUserId).ifPresent(
                    accountServiceImpl::createAccount
            );
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("signupRequest", signupRequest);
            return "redirect:/signup";
        }
    }
}
