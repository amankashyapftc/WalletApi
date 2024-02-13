package com.example.Wallet.exceptions;

public class InsufficientBalanceException extends Exception{

    public InsufficientBalanceException(String message) {
        super(message);
    }
}