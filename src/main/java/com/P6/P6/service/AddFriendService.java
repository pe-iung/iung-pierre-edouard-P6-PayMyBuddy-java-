package com.P6.P6.service;

import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@AllArgsConstructor
@Service
public class AddFriendService {

    private final UserEntityRepository userEntityRepository;

    public void addFriendToUserEntity(String userEntityEmail, String friendEmail, Model model) {
        try {
            // Find the user by email
            UserEntity user = userEntityRepository.findByEmail(userEntityEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with Id: " + userEntityEmail));

            // Find the friend by email
            UserEntity friend = userEntityRepository.findByEmail(friendEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Friend not found with email: " + friendEmail));

            // Add the friend if not already added
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
