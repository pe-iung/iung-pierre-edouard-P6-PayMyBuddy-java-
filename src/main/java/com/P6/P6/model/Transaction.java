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
    private int id;

    @JoinColumn
    @ManyToOne
    private UserEntity sender;

    @JoinColumn
    @ManyToOne
    private UserEntity receiver;

    @Column
    private String description;

    @Column
    private double amount;



}
