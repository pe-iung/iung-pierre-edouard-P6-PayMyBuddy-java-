package com.P6.P6.service;

import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddFriendServiceImplImplTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private Model model;

    @InjectMocks
    private UserService addFriendServiceImpl;


    @Test
    void addFriendToUserEntity_Success() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setFriends(new ArrayList<>());

        UserEntity friend = new UserEntity();
        friend.setEmail("friend@example.com");

        when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userEntityRepository.findByEmail("friend@example.com")).thenReturn(Optional.of(friend));

        // Act
        addFriendServiceImpl.addFriendToUserEntity("user@example.com", "friend@example.com");

        // Assert
        verify(userEntityRepository).save(user);
        verify(model).addAttribute("successMessage", "Friend added successfully!");
    }

    @Test
    void addFriendToUserEntity_UserNotFound() {
        // Arrange
        when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        // Act
        addFriendServiceImpl.addFriendToUserEntity("user@example.com", "friend@example.com");

        // Assert
        verify(model).addAttribute(eq("errorMessage"), any(String.class));
        verify(userEntityRepository, never()).save(any());
    }

    @Test
    void addFriendToUserEntity_FriendNotFound() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");

        when(userEntityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userEntityRepository.findByEmail("friend@example.com")).thenReturn(Optional.empty());

        // Act
        addFriendServiceImpl.addFriendToUserEntity("user@example.com", "friend@example.com");

        // Assert
        verify(model).addAttribute(eq("errorMessage"), any(String.class));
        verify(userEntityRepository, never()).save(any());
    }
}
