package com.P6.P6.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "transaction_table")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn
    private Integer senderId;

    @JoinColumn
    private Integer receiverId;

    @Column
    private String description;

    @Column
    private Double amount;


    public Transaction(Integer senderId, Integer receiverId, double amount, String description) {

        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.description = description;
    }
}
