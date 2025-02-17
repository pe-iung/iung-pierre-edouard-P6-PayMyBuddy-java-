package com.P6.P6.controller;

import com.P6.P6.DTO.FriendListResponse;
import com.P6.P6.service.SecurityHelper;
import com.P6.P6.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FriendController {

    private final UserService userService;

    @GetMapping("/friends")
    public String getFriendList(Model model) {
        try {
            FriendListResponse friendList =
                    userService.getFriendList(SecurityHelper.getConnectedUser());
            model.addAttribute("friendList", friendList);
            log.info("a friendList has been generated controller side from friendService {}",friendList);
            return "friends";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching friend list: " + e.getMessage());
            return "error";
        }
    }
}
