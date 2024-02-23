package com.example.Wallet.exceptions;

public class SameWalletsForTransactionException extends Exception{

    public SameWalletsForTransactionException(String message){
        super(message);
    }
}
