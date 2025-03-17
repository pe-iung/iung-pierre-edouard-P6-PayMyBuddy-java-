package com.P6.P6.DTO;

import com.P6.P6.model.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfilDisplay(String username, String email) {

    public  UserProfilDisplay(UserEntity user){
        this(user.getUsername(), user.getEmail());
    }

}
