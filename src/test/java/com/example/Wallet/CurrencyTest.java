package com.example.Wallet;

import com.example.Wallet.enums.Currency;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyTest {
    @Test
    public void convertDollarToRupee() {
        Currency currency2 = Currency.USD;
        double amount = 10;

        double actual = currency2.convertToBase(amount);
        assertEquals(830, actual);
    }

    @Test
    public void convertRupeeToDollar() {
        Currency currency2 = Currency.INR;
        double amount = 10;

        double actual = currency2.convertToBase(amount);
        assertEquals(10, actual);
    }
}
