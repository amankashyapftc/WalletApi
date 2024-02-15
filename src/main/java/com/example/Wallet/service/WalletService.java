package com.example.Wallet.service;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.entities.WalletResponseModel;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.NoWalletPresentException;
import com.example.Wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.AbstractList;
import java.util.List;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;


    public WalletResponseModel deposit(Long id, Money amount) throws InvalidAmountException {
        Wallet wallet = walletRepository.findById(id).orElseThrow(()->new RuntimeException("No wallet found with this id " + id));
        wallet.deposit(amount);
        walletRepository.save(wallet);
        return new WalletResponseModel(wallet.getId(),wallet.getMoney());
    }

    public WalletResponseModel withdraw(Long id, Money amount) throws InsufficientBalanceException, InvalidAmountException {
        Wallet wallet = walletRepository.findById(id).orElseThrow(()->new RuntimeException("No wallet found with this id " + id));
        wallet.withdraw(amount);
        walletRepository.save(wallet);
        return new WalletResponseModel(wallet.getId(),wallet.getMoney());
    }

    public WalletResponseModel createWallet() throws InvalidAmountException {
        Wallet wallet = new Wallet();
        walletRepository.save(wallet);
        return new WalletResponseModel(wallet.getId(), wallet.getMoney());
    }

    public Money checkBalance(Long id) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(()->new RuntimeException("No wallet found with this id " + id));
        return wallet.getMoney();
    }

    public List<WalletResponseModel> getAllWallets() throws NoWalletPresentException {
        List<WalletResponseModel> walletList = walletRepository.findAll().stream().map(wallet -> new WalletResponseModel(wallet.getId(), wallet.getMoney())).toList();
        return walletList;
    }
}

