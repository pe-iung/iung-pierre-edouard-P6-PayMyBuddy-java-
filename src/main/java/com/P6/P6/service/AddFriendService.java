package com.P6.P6.service;

import org.springframework.ui.Model;

public interface AddFriendService {
    void addFriendToUserEntity(String userEntityEmail, String friendEmail);
}
