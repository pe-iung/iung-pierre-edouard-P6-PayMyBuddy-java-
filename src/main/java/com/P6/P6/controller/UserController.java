package com.P6.P6.controller;

import com.P6.P6.DTO.AddUserRequest;
import com.P6.P6.model.User;
import com.P6.P6.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserRepository userRepository;

    @PostMapping("/create")
    public String create(@RequestBody AddUserRequest addUserRequest){
// save a single Customer
        //todo : check how to add a user without the Id (is it by adding a new constructor?)
        userRepository.save(
                new User(
                addUserRequest.getUserName(),
                addUserRequest.getPassword(),
                addUserRequest.getEmail(),
                addUserRequest.getConnections()
                ));



        return "Customer is created";
    }

}
