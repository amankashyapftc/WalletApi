package com.example.Wallet.entities;

import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;

import com.example.Wallet.grpcClient.CurrencyConverterClient;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import proto.ConvertResponse;


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
        CurrencyConverterClient converter = new CurrencyConverterClient();
        ConvertResponse res = converter.convertMoney(money, this.currency ,this.currency);
        if(res.getMoney().getAmount() <= 0)
            throw new InvalidAmountException("Money Should be greater than 0");

        this.amount += res.getMoney().getAmount();
    }

    public void subtract(Money money) throws InvalidAmountException, InsufficientBalanceException {
        CurrencyConverterClient converter = new CurrencyConverterClient();
        ConvertResponse res = converter.convertMoney(money, this.currency ,this.currency);

        if(res.getMoney().getAmount() > this.amount)
            throw new InsufficientBalanceException("Insufficient Balance");

        if(res.getMoney().getAmount() <= 0)
            throw new InvalidAmountException("Money Should be greater than 0");

        this.amount -= res.getMoney().getAmount();
    }
}
