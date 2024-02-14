package com.example.Wallet.enums;

import java.util.function.Function;

public enum Currency {
    INR(amount->amount),
    USD(amount->amount*83);
    private final Function<Double, Double> convertToBase;
    private Currency(Function<Double, Double> convertToBase) {
        this.convertToBase = convertToBase;
    }
    public Double convertToBase(Double amount) {
        return this.convertToBase.apply(amount);
    }
}
