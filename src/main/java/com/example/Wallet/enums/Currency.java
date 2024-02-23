package com.example.Wallet.enums;

import java.util.function.Function;

public enum Currency {
    INR(1.0),
    USD(83.10);

    private final double conversionFactor;

    Currency(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }
}
