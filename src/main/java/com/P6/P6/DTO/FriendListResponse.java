package com.P6.P6.DTO;

import com.P6.P6.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendListResponse {
    private List<FriendDTO> friends;
    public FriendListResponse(UserEntity user) {

        this(user.getFriends().stream().map(
                FriendDTO::new
        ).toList());

    }
}



