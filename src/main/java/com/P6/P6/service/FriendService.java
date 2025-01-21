package com.P6.P6.service;

import com.P6.P6.DTO.FriendListResponse;
import com.P6.P6.model.UserEntity;
import org.springframework.transaction.annotation.Transactional;

public interface FriendService {
    @Transactional(readOnly = true)
    FriendListResponse getFriendList(UserEntity currentUser);
}
