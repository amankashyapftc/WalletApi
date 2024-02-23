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
    private Long transactionId;

    private LocalDateTime timestamp;

    private Money money;

    @ManyToOne(cascade = CascadeType.ALL)
    private User sender;

    private Long senderWalletId;

    @ManyToOne(cascade = CascadeType.ALL)
    private User receiver;

    private Long receiverWalletId;

    public Transaction(LocalDateTime timestamp, Money money, User sender, Long senderWalletId, User receiver, Long receiverWalletId) {
        this.timestamp = timestamp;
        this.money = money;
        this.sender = sender;
        this.senderWalletId = senderWalletId;
        this.receiver = receiver;
        this.receiverWalletId = receiverWalletId;
    }
}
