package com.P6.P6.repositories;

import com.P6.P6.model.Account;
import com.P6.P6.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUser(UserEntity user);
}
