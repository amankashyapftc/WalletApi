package com.example.Wallet;

import com.example.Wallet.entities.Money;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyTest {
    @Test
    public void addValidMoney() throws InvalidAmountException {
        Money money = new Money(100, Currency.INR);
        Money money1 = new Money(100, Currency.INR);
        money.add(money1);

        assertEquals(200, money.getAmount());
    }

    @Test
    public void addTwoValidDifferentMoney() throws InvalidAmountException {
        Money money = new Money(100, Currency.INR);
        Money money1 = new Money(100, Currency.USD);
        money.add(money1);

        assertEquals(8400, money.getAmount());
    }

    @Test
    public void moneyWithNegativeAmount() {
        assertThrows(InvalidAmountException.class, () -> new Money(-100, Currency.INR));
    }

    @Test
    public void subtractValidMoney() throws InsufficientBalanceException, InvalidAmountException {
        Money money = new Money(100, Currency.INR);
        Money money1 = new Money(100, Currency.INR);
        money.subtract(money1);

        assertEquals(0, money.getAmount());
    }

    @Test
    public void subtractTwoValidDifferentMoney() throws InvalidAmountException, InsufficientBalanceException {
        Money money = new Money(100, Currency.INR);
        Money money1 = new Money(1, Currency.USD);
        money.subtract(money1);

        assertEquals(17, money.getAmount());
    }

    @Test
    public void subtractMoneyFromLesserMoney() throws InvalidAmountException {
        Money money = new Money(100, Currency.INR);
        Money money1 = new Money(100, Currency.USD);

        assertThrows(InsufficientBalanceException.class, () -> money.subtract(money1));
    }
}