package com.P6.P6.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "user_table")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "password")
    private String password;



    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "userEntity_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<UserEntity> friends = new ArrayList<>();

    public UserEntity(String userName, String password, String email) {
        this.username = userName;
        this.password = password;
        this.email = email;
    }


    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of();
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return email;
    }
}
