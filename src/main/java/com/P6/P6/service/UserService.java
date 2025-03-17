package com.P6.P6.service;

import com.P6.P6.DTO.FriendListResponse;
import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.DTO.UserEditProfilRequest;
import com.P6.P6.DTO.UserProfilDisplay;
import com.P6.P6.model.UserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {

    @Transactional
    Integer signupNewUser(SignupRequest signupRequest);

    @Transactional(readOnly = true)
    List<UserEntity> getFriendList(UserEntity currentUser);

    void addFriendToUserEntity(String userEntityEmail, String friendEmail);

//    UserProfilDisplay convertToUserDisplay(Integer userId);

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(Integer userId);

    Integer editUser(Integer currentUserId, UserEditProfilRequest userEditProfilRequest);
}
