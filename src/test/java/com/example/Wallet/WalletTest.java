package com.example.Wallet;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletTest {
    @Test
    void expectMoneyDeposited() throws InvalidAmountException {
        Wallet wallet = new Wallet(1L,new Money(100.0, Currency.INR));
        wallet.deposit(new Money(100.0, Currency.INR));

        Wallet expected = new Wallet(1L,new Money(200.0, Currency.INR));

        assertEquals(expected, wallet);
    }

    @Test
    void expectMoneyWithdrawn() throws InsufficientBalanceException, InvalidAmountException {
        Wallet wallet = new Wallet(1L,new Money(0.0, Currency.INR));
        wallet.deposit(new Money(100.0, Currency.INR));
        wallet.withdraw(new Money(50.0, Currency.INR));

        Wallet expected = new Wallet(1L, new Money(50.0, Currency.INR));

        assertEquals(expected, wallet);
    }

    @Test
    void expectExceptionForInsufficientBalanceWithdrawn() throws InsufficientBalanceException, InvalidAmountException {
        Wallet wallet = new Wallet();
        assertThrows(InsufficientBalanceException.class, ()-> wallet.withdraw(new Money(100.0, Currency.INR)));
    }
}
