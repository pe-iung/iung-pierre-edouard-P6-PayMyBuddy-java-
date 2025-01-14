package com.P6.P6.service;

import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@RequiredArgsConstructor
@Service
public class AddFriendService {

    private final UserEntityRepository userEntityRepository;

    public void addFriendToUserEntity(String userEntityEmail, String friendEmail, Model model) {
        try {

            UserEntity user = userEntityRepository.findByEmail(userEntityEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with Id: " + userEntityEmail));


            UserEntity friend = userEntityRepository.findByEmail(friendEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Friend not found with email: " + friendEmail));


            if (!user.getFriends().contains(friend)) {
                user.getFriends().add(friend);
                userEntityRepository.save(user);
                model.addAttribute("successMessage", "Friend added successfully!");
            } else {
                model.addAttribute("errorMessage", "Friend is already added!");
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
    }
}
