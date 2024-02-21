package com.example.Wallet.entities;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int transactionId;

    private LocalDateTime timestamp;

    @Embedded
    private Money money;

    @ManyToOne(cascade = CascadeType.ALL)
    private User sender;

    @ManyToOne(cascade = CascadeType.ALL)
    private User receiver;

    public Transaction(LocalDateTime timestamp, Money money, User sender, User receiver) {
        this.timestamp = timestamp;
        this.money = money;
        this.sender = sender;
        this.receiver = receiver;
    }
}
