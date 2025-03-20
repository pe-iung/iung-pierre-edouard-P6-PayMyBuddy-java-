package com.P6.P6.controller;

import com.P6.P6.model.UserEntity;
import com.P6.P6.service.SecurityHelper;
import com.P6.P6.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AddFriendController {

    private final UserService userService;

    @GetMapping("/add-friend")
    public String showAddFriendForm() {

        return "add-friend";
    }

    @PostMapping("/add-friend")
    public String addFriend(
            String friendEmail,
            RedirectAttributes redirectAttributes
    ) {

        try {
        final UserEntity connectedUser = SecurityHelper.getConnectedUser();
        userService.addFriendToUserEntity(connectedUser.getEmail(),friendEmail);
        redirectAttributes.addFlashAttribute("successMessage", "Ami ajouté avec succès!");

        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:add-friend";
    }
}
