package com.example.Wallet.enums;

import static com.example.Wallet.enums.Currency.INR;
import static com.example.Wallet.enums.Currency.USD;

public enum Country {
    INDIA(INR),
    USA(USD);

    private final Currency currency;

    Country(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return this.currency;
    }
}
