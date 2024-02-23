package com.example.Wallet.entities;

import com.example.Wallet.enums.Country;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long walletId;

    private Money money;

    public Wallet(Country country) {
        this.money = new Money(0.0, country.getCurrency());
    }

    public void deposit(Money money) throws InvalidAmountException {
        this.money.add(money);
    }

    public void withdraw(Money money) throws InsufficientBalanceException, InvalidAmountException {
        this.money.subtract(money);
    }

}
