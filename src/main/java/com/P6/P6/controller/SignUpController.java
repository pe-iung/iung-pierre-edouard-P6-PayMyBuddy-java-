package com.P6.P6.controller;

import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SignUpController {


    private final SignupService signupService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String handleSignup(@ModelAttribute SignupRequest signupRequest, Model model) {

        signupService.signupNewUser(signupRequest);

        model.addAttribute("message", "Signup successful!");
        return "redirect:/login";
    }
}
