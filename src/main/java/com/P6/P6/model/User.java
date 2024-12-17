package com.P6.P6.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//todo : check if the anotation are the proper ones
@Entity
@Table(name = "user_table")

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "username")
    private String username;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "password")
    private String password;

    //todo check how to link user conections with user, maybe a list of UserID ?
    //ou plutot    private List<User> connections;
    @ManyToMany
    private List<User> connections = new ArrayList<>();

    public User(String userName, String password, String email) {
        this.username = userName;
        this.password = password;
        this.email = email;
    }
}
