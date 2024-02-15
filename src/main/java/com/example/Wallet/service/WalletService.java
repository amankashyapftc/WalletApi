package com.example.Wallet.service;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.NoWalletPresentException;
import com.example.Wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;


    public Wallet deposit(Long id, Money amount) throws InvalidAmountException {
        Wallet wallet = walletRepository.findById(id).orElseThrow(()->new RuntimeException("No wallet found with this id " + id));
        wallet.deposit(amount);
        return walletRepository.save(wallet);
    }

    public Wallet withdraw(Long id, Money amount) throws InsufficientBalanceException, InvalidAmountException {
        Wallet wallet = walletRepository.findById(id).orElseThrow(()->new RuntimeException("No wallet found with this id " + id));
        wallet.withdraw(amount);
        return walletRepository.save(wallet);
    }

    public Wallet createWallet() throws InvalidAmountException {
        Wallet wallet = new Wallet();
        return walletRepository.save(wallet);
    }

    public Money checkBalance(Long id) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(()->new RuntimeException("No wallet found with this id " + id));
        return wallet.getMoney();
    }

    public List<Wallet> getAllWallets() throws NoWalletPresentException {
        List<Wallet> walletList = walletRepository.findAll();
        if(walletList.isEmpty()) throw new NoWalletPresentException("No Wallets Available.");
        return walletList;
    }
}

