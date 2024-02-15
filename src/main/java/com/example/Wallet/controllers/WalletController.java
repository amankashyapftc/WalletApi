package com.example.Wallet.controllers;

import com.example.Wallet.entities.WalletRequestModel;
import com.example.Wallet.entities.WalletResponseModel;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.NoWalletPresentException;
import com.example.Wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractList;
import java.util.List;

@RestController
public class WalletController {
    @Autowired
    private WalletService walletService;
    @GetMapping("/hello")
    public String hello() {
        return "Hello, world!";
    }
    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }
    @PostMapping("/")
    public ResponseEntity<WalletResponseModel> createWallet() throws InvalidAmountException {
        return ResponseEntity.ok(walletService.createWallet());
    }
    @PutMapping("deposit/{id}")
    public ResponseEntity<WalletResponseModel> deposit(@PathVariable Long id, @RequestBody WalletRequestModel walletRequestModel) throws InvalidAmountException {
       return ResponseEntity.ok(walletService.deposit(id,walletRequestModel.getMoney()));
    }
    @PutMapping("withdraw/{id}")
    public ResponseEntity<WalletResponseModel> withdraw(@PathVariable Long id, @RequestBody WalletRequestModel walletRequestModel) throws InsufficientBalanceException, InvalidAmountException {
        return ResponseEntity.ok(walletService.withdraw(id,walletRequestModel.getMoney()));
    }

    @GetMapping("/wallets")
    public ResponseEntity<List<WalletResponseModel>>  getWallets() throws NoWalletPresentException {
        return ResponseEntity.ok(walletService.getAllWallets());
    }
}
