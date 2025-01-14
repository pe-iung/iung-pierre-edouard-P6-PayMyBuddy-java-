package com.P6.P6.controller;

import com.P6.P6.service.AddFriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class addFriendController {

    private final AddFriendService addFriendService;

    @GetMapping("/add-friend")
    public String showAddFriendForm() {
        return "add-friend";
    }
    @PostMapping("/add-friend")
    public String addFriend(
            @RequestParam String userEmail,
            @RequestParam String friendEmail,
            Model model
    ) {
        addFriendService.addFriendToUserEntity(userEmail,friendEmail, model);
        return "add-friend";
    }
}
