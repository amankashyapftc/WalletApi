package com.example.Wallet.controllers;

import com.example.Wallet.entities.Wallet;
import com.example.Wallet.entities.WalletRequestModel;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Wallet createWallet(){
        return walletService.createWallet();
    }
    @PutMapping("deposit/{id}")
    public Wallet deposit(@PathVariable Long id, @RequestBody WalletRequestModel walletRequestModel) throws InvalidAmountException {
       return walletService.deposit(id,walletRequestModel.getMoney());
    }
    @PutMapping("withdraw/{id}")
    public Wallet withdraw(@PathVariable Long id,@RequestBody WalletRequestModel walletRequestModel) throws InsufficientBalanceException, InvalidAmountException {
        return walletService.withdraw(id,walletRequestModel.getMoney());
    }
}
