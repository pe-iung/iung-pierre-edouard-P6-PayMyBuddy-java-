package com.P6.P6.controller;

import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SignUpController {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String handleSignup(@ModelAttribute SignupRequest signupRequest, Model model) {
        // Save user to the database
        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserEntity newUser = new UserEntity(
                signupRequest.getUsername(),
                encodedPassword,
                signupRequest.getEmail()
        );

        userEntityRepository.save(newUser);

        model.addAttribute("message", "Signup successful!");
        return "redirect:/login";
    }
}
