package com.P6.P6.service;

import com.P6.P6.DTO.FriendListResponse;
import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.model.UserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserService {



    @Transactional
    UserEntity signupNewUser(SignupRequest signupRequest);

    @Transactional(readOnly = true)
    FriendListResponse getFriendList(UserEntity currentUser);

    void addFriendToUserEntity(String userEntityEmail, String friendEmail);

    Optional<UserEntity> findByEmail(String email);
}
