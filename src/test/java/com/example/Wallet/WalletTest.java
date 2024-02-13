package com.example.Wallet;

import com.example.Wallet.entities.Wallet;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletTest {
    @Test
    void expectMoneyDeposited() throws InvalidAmountException {
        Wallet wallet = new Wallet(1L,100);
        wallet.deposit(100);

        Wallet expected = new Wallet(1L,200);

        assertEquals(expected, wallet);
    }

    @Test
    void expectMoneyWithdrawn() throws InsufficientBalanceException, InvalidAmountException {
        Wallet wallet = new Wallet(1L,0);
        wallet.deposit(100);
        wallet.withdraw(50);

        Wallet expected = new Wallet(1L, 50);

        assertEquals(expected, wallet);
    }

    @Test
    void expectExceptionForInsufficientBalanceWithdrawn() throws InsufficientBalanceException {
        Wallet wallet = new Wallet();
        assertThrows(InsufficientBalanceException.class, ()-> wallet.withdraw(100));
    }
}
