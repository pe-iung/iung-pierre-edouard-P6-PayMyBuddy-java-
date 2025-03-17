package com.P6.P6.repositories;

import com.P6.P6.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(Integer userId);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.friends WHERE u.id = :id")
    Optional<UserEntity> findWithFriendsById(@Param("id") Integer id);
}

