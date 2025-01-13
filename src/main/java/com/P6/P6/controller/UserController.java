package com.P6.P6.controller;

import com.P6.P6.DTO.AddUserRequest;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserRepository userRepository;

    @PostMapping("/create")
    public String create(@RequestBody AddUserRequest addUserRequest){

        userRepository.save(
                new UserEntity(
                addUserRequest.getUsername(),
                addUserRequest.getPassword(),
                addUserRequest.getEmail()
                )
        );



        return "index";
    }

}
