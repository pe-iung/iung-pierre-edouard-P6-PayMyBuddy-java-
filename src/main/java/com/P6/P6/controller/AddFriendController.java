package com.P6.P6.controller;

import com.P6.P6.model.UserEntity;
import com.P6.P6.service.SecurityHelper;
import com.P6.P6.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AddFriendController {

    private final UserService userService;

    @GetMapping("/add-friend")
    public String showAddFriendForm(Model model) {
        model.addAttribute("currentUri","/add-friend");
        return "add-friend";
    }

    @PostMapping("/add-friend")
    public String addFriend(
            String friendEmail,
            Model model
    ) {

        try {
        final UserEntity connectedUser = SecurityHelper.getConnectedUser();
        userService.addFriendToUserEntity(connectedUser.getEmail(),friendEmail);

        }catch (Exception e){
            model.addAttribute("errorMessage" , e.getMessage());
        }

        return "add-friend";
    }
}
