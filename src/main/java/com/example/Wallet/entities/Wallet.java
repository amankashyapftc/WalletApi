package com.example.Wallet.entities;

import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Entity
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private Money money;


    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private  User user;


    public Wallet() throws InvalidAmountException {
        this.money = new Money(0.0, Currency.INR);
    }

    public void deposit(Money money) {
       this.money.add(money);
    }

    public void withdraw(Money money) throws InsufficientBalanceException{
        this.money.subtract(money);
    }

}
