package com.example.Wallet.entities;

import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class Money {
    private double amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    public void add(Money money) throws InvalidAmountException {
        if((money.amount/this.currency.getConversionFactor()) * money.currency.getConversionFactor() <= 0)
            throw new InvalidAmountException("Amount must be positive.");

        this.amount += (money.amount/this.currency.getConversionFactor()) * money.currency.getConversionFactor();
    }

    public void subtract(Money money) throws InvalidAmountException, InsufficientBalanceException {
        if((money.amount/this.currency.getConversionFactor()) * money.currency.getConversionFactor() > this.amount)
            throw new InsufficientBalanceException("Insufficient Balance..");

        if((money.amount/this.currency.getConversionFactor()) * money.currency.getConversionFactor() <= 0)
            throw new InvalidAmountException("Amount must be positive.");

        this.amount -= (money.amount/this.currency.getConversionFactor()) * money.currency.getConversionFactor();
    }
}
