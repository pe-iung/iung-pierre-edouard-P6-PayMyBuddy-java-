package com.P6.P6.controller;

import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SignUpController {

    private final SignupService signupService;

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
            RedirectAttributes redirectAttributes
    ) {
        // Add debug logging
        System.out.println("Validation errors: " + bindingResult.getAllErrors());

        // Check for empty fields manually if needed
        if (signupRequest.getUsername() == null || signupRequest.getUsername().trim().isEmpty() ||
                signupRequest.getPassword() == null || signupRequest.getPassword().trim().isEmpty() ||
                signupRequest.getEmail() == null || signupRequest.getEmail().trim().isEmpty()) {
            bindingResult.rejectValue("username", "field.required", "All fields are required");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.signupRequest",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute("signupRequest", signupRequest);
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Please fill in all required fields correctly"
            );
            return "redirect:/signup";
        }

        try {
            signupService.signupNewUser(signupRequest);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Signup successful! Please login."
            );
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("signupRequest", signupRequest);
            return "redirect:/signup";
        }
    }
}
