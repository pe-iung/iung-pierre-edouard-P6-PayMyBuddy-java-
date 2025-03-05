package com.P6.P6.repositories;

import com.P6.P6.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    //List<Transaction> findAllBySenderIdOrReceiverId(Integer senderId, Integer receiverId);
    List<Transaction> findAllBySenderIdOrReceiverId(Integer sender_id, Integer receiver_id);


}