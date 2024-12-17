package com.P6.P6.DTO;

import com.P6.P6.model.User;
import jakarta.persistence.Column;
import lombok.Value;

import java.util.List;

@Value
public class AddUserRequest {

    String userName;

    String email;

    String password;

    public List<Integer> getConnections() {
        return List.of(1,2,3);
    }
}
