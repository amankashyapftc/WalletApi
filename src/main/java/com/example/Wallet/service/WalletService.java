package com.example.Wallet.service;

import com.example.Wallet.entities.Wallet;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;


    public Wallet deposit(Long id, double amount) throws InvalidAmountException {
        Wallet wallet = walletRepository.findById(id).orElseThrow(()->new RuntimeException("No wallet found with this id " + id));
        wallet.deposit(amount);
        return walletRepository.save(wallet);
    }

    public Wallet withdraw(Long id, double amount) throws InsufficientBalanceException, InvalidAmountException {
        Wallet wallet = walletRepository.findById(id).orElseThrow(()->new RuntimeException("No wallet found with this id " + id));
        wallet.withdraw(amount);
        return walletRepository.save(wallet);
    }

    public Wallet createWallet() {
        Wallet wallet = new Wallet();
        return walletRepository.save(wallet);
    }

    public double checkBalance(Long id) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(()->new RuntimeException("No wallet found with this id " + id));
        return wallet.getBalance();
    }
}

