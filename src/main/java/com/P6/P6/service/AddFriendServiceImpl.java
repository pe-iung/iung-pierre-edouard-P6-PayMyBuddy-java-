package com.P6.P6.service;

import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@RequiredArgsConstructor
@Service
@Slf4j
public class AddFriendServiceImpl implements AddFriendService{

    private final UserEntityRepository userEntityRepository;

    @Override
    public void addFriendToUserEntity(String userEntityEmail, String friendEmail) {
            UserEntity user = userEntityRepository.findByEmail(userEntityEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with Id: " + userEntityEmail));


            UserEntity friend = userEntityRepository.findByEmail(friendEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Friend not found with email: " + friendEmail));


            if (user.getFriends().contains(friend)) {
                throw new IllegalArgumentException("Friend is already added!");
            }


            user.getFriends().add(friend);
                log.info("new friend added to {} : {}",userEntityEmail, user.getFriends());
        userEntityRepository.save(user);


    }
}
