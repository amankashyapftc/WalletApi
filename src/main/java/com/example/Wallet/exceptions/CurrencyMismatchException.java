package com.example.Wallet.exceptions;

public class CurrencyMismatchException extends Exception{
    public CurrencyMismatchException(String message){
        super(message);
    }
}
