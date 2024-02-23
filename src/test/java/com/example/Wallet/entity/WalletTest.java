package com.example.Wallet.entity;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Country;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WalletTest {
    @Mock
    private Money money;

    @Test
    void testMoneyDeposited() throws InvalidAmountException {
        Wallet wallet = new Wallet(1L,money);
        Money moneyToAdd = new Money(100, Currency.INR);

        wallet.deposit(moneyToAdd);

        verify(money, times(1)).add(moneyToAdd);
    }

    @Test
    void testExceptionForInvalidAmountDeposited() {
        Wallet wallet = new Wallet(Country.INDIA);
        assertThrows(InvalidAmountException.class,()-> wallet.deposit(new Money(-50, Currency.INR)));
    }

    @Test
    void testMoneyWithdrawn() throws InsufficientBalanceException, InvalidAmountException {
        Wallet wallet = new Wallet(1L, money);
        Money moneyToAdd = new Money(100, Currency.INR);
        Money moneyToWithdraw = new Money(50, Currency.INR);

        wallet.deposit(moneyToAdd);
        wallet.withdraw(moneyToWithdraw);

        verify(money, times(1)).add(moneyToAdd);
        verify(money, times(1)).subtract(moneyToWithdraw);
    }

    @Test
    void testExceptionForInsufficientBalanceWhenWithdrawing(){
        Wallet wallet = new Wallet(Country.INDIA);
        assertThrows(InsufficientBalanceException.class, ()-> wallet.withdraw(new Money(100, Currency.INR)));
    }

    @Test
    void testExceptionForInvalidAmountWithdrawn() {
        Wallet wallet = new Wallet(Country.INDIA);
        assertThrows(InvalidAmountException.class,()-> wallet.withdraw(new Money(-50, Currency.INR)));
    }

    @Test
    void testCurrencyINRForIndia() {
        Wallet wallet = new Wallet(Country.INDIA);

        assertEquals(new Money(0.0,Currency.INR), wallet.getMoney());
    }

    @Test
    void testCurrencyUSDForUSA() {
        Wallet wallet = new Wallet(Country.USA);

        assertEquals(new Money(0.0,Currency.USD), wallet.getMoney());
    }
}
