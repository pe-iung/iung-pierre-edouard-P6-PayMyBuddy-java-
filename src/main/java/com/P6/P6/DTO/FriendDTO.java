package com.P6.P6.DTO;
import com.P6.P6.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendDTO {
    private Integer id;
    private String username;
    private String email;

    public FriendDTO(UserEntity user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
