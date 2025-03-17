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
    @ManyToOne
    private UserEntity sender;

    @JoinColumn
    @ManyToOne
    private UserEntity receiver;

    @Column
    private String description;

    @Column
    private int amountInCents; //initialAmount

    @Column
    private int feeAmountInCents;


    public Transaction(UserEntity sender, UserEntity receiver, int fee, int amountInCents, String description) {

        this.sender = sender;
        this.receiver = receiver;
        this.feeAmountInCents = fee;
        this.amountInCents = amountInCents;
        this.description = description;
    }


    public double amountAfterFee(){
        return amountInCents - feeAmountInCents;
    }

}
