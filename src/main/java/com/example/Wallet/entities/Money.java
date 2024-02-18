package com.example.Wallet.entities;

import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Money {
    private double amount;

    @Enumerated
    private Currency currency;

    public Money(double amount, Currency currency) throws InvalidAmountException {
        if (amount < 0) {
            throw new InvalidAmountException("Money should be positive.");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public void subtract(Money money) throws InsufficientBalanceException {
        double amountInBaseCurrency = money.getCurrency().convertToBase(money.getAmount());
        if (this.amount < amountInBaseCurrency) {
            throw new InsufficientBalanceException("Don't have enough money.");
        }
        this.amount = this.amount - amountInBaseCurrency;
    }

    public void add(Money money) {
        double amountInBaseCurrency = money.getCurrency().convertToBase(money.getAmount());
        this.amount = this.amount + amountInBaseCurrency;
    }
}
