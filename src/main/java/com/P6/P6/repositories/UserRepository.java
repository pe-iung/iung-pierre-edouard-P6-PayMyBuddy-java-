package com.P6.P6.repositories;

import com.P6.P6.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findById(int userId);

    List<User> findAll();
}

