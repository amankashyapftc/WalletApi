package com.example.Wallet.entity;

import com.example.Wallet.entities.Money;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    @Test
    void testMoneyCreated() {
        assertDoesNotThrow(()-> new Money(10, Currency.INR));
    }

    @Test
    void testMoney100Added() throws InvalidAmountException {
        Money money = new Money(0,Currency.INR);

        money.add(new Money(100, Currency.INR));

        assertEquals(new Money(100, Currency.INR), money);
    }

    @Test
    void testMoney50Added() throws InvalidAmountException {
        Money money = new Money(0,Currency.INR);

        money.add(new Money(50, Currency.INR));

        assertEquals(new Money(50, Currency.INR), money);
    }

    @Test
    void testMoney100USDAdded() throws InvalidAmountException {
        Money money = new Money(0,Currency.INR);

        money.add(new Money(100, Currency.USD));

        assertEquals(new Money(8310.0, Currency.INR), money);
    }


    @Test
    void testMoney100INRSubtracted() throws InvalidAmountException, InsufficientBalanceException {
        Money money = new Money(100,Currency.INR);

        money.subtract(new Money(50, Currency.INR));

        assertEquals(new Money(50, Currency.INR), money);
    }

    @Test
    void testMoney100USDSubtracted() throws InvalidAmountException, InsufficientBalanceException {
        Money money = new Money(0,Currency.INR);
        money.add(new Money(100, Currency.USD));

        money.subtract(new Money(100, Currency.USD));

        assertEquals(new Money(0.0, Currency.INR), money);
    }


    @Test
    void testExceptionForInsufficientBalance() {
        Money money = new Money(10,Currency.INR);

        assertThrows(InsufficientBalanceException.class, ()-> money.subtract(new Money(50, Currency.INR)));
    }

    @Test
    void testExceptionAddingNegativeMoney() {
        Money money = new Money(100,Currency.INR);

        assertThrows(InvalidAmountException.class, ()-> money.subtract(new Money(-50, Currency.INR)));
    }

    @Test
    void test1USDWhenAdding83_10INR() throws InvalidAmountException {
        Money money = new Money(0.0, Currency.USD);

        money.add(new Money(83.10, Currency.INR));

        assertEquals(new Money(1.0, Currency.USD), money);
    }


    @Test
    void testExceptionWhenSubtracting1EURFrom1USD() throws InvalidAmountException, InsufficientBalanceException {
        Money money = new Money(0.0, Currency.USD);

        assertThrows(InsufficientBalanceException.class, ()-> money.subtract(new Money(1, Currency.USD)));
    }

}
