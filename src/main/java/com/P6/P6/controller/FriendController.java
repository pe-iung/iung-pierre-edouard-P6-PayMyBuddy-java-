package com.P6.P6.controller;

import com.P6.P6.DTO.FriendListResponse;
import com.P6.P6.service.FriendService;
import com.P6.P6.service.SecurityHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/friends")
    public String getFriendList(Model model) {
        try {
            FriendListResponse friendList =
                    friendService.getFriendList(SecurityHelper.getConnectedUser());
            model.addAttribute("friendList", friendList);
            log.info("a friendList hasbeen generated controller side from friendService {}",friendList);
            return "friends";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching friend list: " + e.getMessage());
            return "error";
        }
    }
}
