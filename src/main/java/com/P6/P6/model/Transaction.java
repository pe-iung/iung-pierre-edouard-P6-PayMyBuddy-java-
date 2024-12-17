package com.P6.P6.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.P6.P6.model.User;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Transaction {
    @Id
    @Column
    private int id;

    @Column
    private int senderId;
    @Column
    private int receiverId;
    @Column
    private String description;
    @Column
    private double amount;



}
