package com.P6.P6.controller;

import com.P6.P6.model.UserEntity;
import com.P6.P6.service.AddFriendService;
import com.P6.P6.service.SecurityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AddFriendController {

    private final AddFriendService addFriendService;

    @GetMapping("/add-friend")
    public String showAddFriendForm() {
        return "add-friend";
    }

    @PostMapping("/add-friend")
    public String addFriend(
            String friendEmail,
            Model model
    ) {

        try {
        final UserEntity connectedUser = SecurityHelper.getConnectedUser();
        addFriendService.addFriendToUserEntity(connectedUser.getEmail(),friendEmail);

        }catch (Exception e){
            model.addAttribute("errorMessage" , e.getMessage());
        }

        return "add-friend";
    }
}
