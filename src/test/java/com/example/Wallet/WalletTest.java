package com.example.Wallet;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class WalletTest {

    @Mock
    private Wallet wallet;
    @Test
    void testMoneyDeposited() throws InvalidAmountException {
        wallet.deposit(new Money(100.0, Currency.INR));

        verify(wallet,times(1)).deposit(any());
    }

    @Test
    void testMoneyWithdrawn() throws InsufficientBalanceException, InvalidAmountException {
        wallet.deposit(new Money(100.0, Currency.INR));
        wallet.withdraw(new Money(50.0, Currency.INR));

        verify(wallet,times(1)).deposit(any());
        verify(wallet,times(1)).withdraw(any());

    }

    @Test
    void testExceptionForInsufficientBalanceWithdrawn() throws InvalidAmountException {
        Wallet wallet = new Wallet();
        assertThrows(InsufficientBalanceException.class, ()-> wallet.withdraw(new Money(100.0, Currency.INR)));
    }
    @Test
    void testExceptionForInvalidAmountDeposited() throws InvalidAmountException {
        Wallet wallet = new Wallet(1L,new Money(100,Currency.INR));
        assertThrows(InvalidAmountException.class,()-> wallet.deposit(new Money(-50, Currency.INR)));
    }
    @Test
    void testExceptionForInvalidAmountWithdrawn() throws InvalidAmountException {
        Wallet wallet = new Wallet(1L,new Money(0,Currency.INR));
        assertThrows(InvalidAmountException.class,()-> wallet.withdraw(new Money(-50, Currency.INR)));
    }
}
