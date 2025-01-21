package com.P6.P6.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendListResponse {
    private List<FriendDTO> friends;
    private int totalFriends;
}

