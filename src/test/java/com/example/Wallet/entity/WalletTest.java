package com.example.Wallet.entity;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
public class WalletTest {
    @Mock
    private Money money;
    @InjectMocks
    private Wallet wallet;
    @BeforeEach
    void setup(){
        openMocks(this);
    }
    @Test
    void testMoneyDeposited() throws InvalidAmountException {
        Money depositeMoney = new Money(100.0, Currency.INR);
        wallet.deposit(depositeMoney);

        verify(money,times(1)).add(depositeMoney);
    }

    @Test
    void testMoneyWithdrawn() throws InsufficientBalanceException, InvalidAmountException {
        Money depositMoney = new Money(100.0, Currency.INR);
        Money withdrawMoney = new Money(50.0, Currency.INR);
        wallet.deposit(depositMoney);
        wallet.withdraw(withdrawMoney);

        verify(money,times(1)).add(depositMoney);
        verify(money,times(1)).subtract(withdrawMoney);

    }

    @Test
    void testExceptionForInsufficientBalanceWithdrawn() throws InvalidAmountException {
        Wallet wallet = new Wallet();
        assertThrows(InsufficientBalanceException.class, ()-> wallet.withdraw(new Money(100.0, Currency.INR)));
    }
    @Test
    void testExceptionForInvalidAmountDeposited() throws InvalidAmountException {
        Wallet wallet = new Wallet();
        assertThrows(InvalidAmountException.class,()-> wallet.deposit(new Money(-50, Currency.INR)));
    }
    @Test
    void testExceptionForInvalidAmountWithdrawn() throws InvalidAmountException {
        Wallet wallet = new Wallet();
        assertThrows(InvalidAmountException.class,()-> wallet.withdraw(new Money(-50, Currency.INR)));
    }
}
