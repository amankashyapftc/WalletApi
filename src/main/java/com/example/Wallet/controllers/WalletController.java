package com.example.Wallet.controllers;

import com.example.Wallet.exceptions.AuthenticationFailedException;
import com.example.Wallet.requestModels.WalletRequestModel;
import com.example.Wallet.responseModels.WalletResponseModel;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.NoWalletPresentException;
import com.example.Wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/deposit")
    public ResponseEntity<WalletResponseModel> deposit(@RequestBody WalletRequestModel walletRequestModel) throws AuthenticationFailedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        WalletResponseModel returnedResponse = new WalletResponseModel(walletService.deposit(username, walletRequestModel).getMoney());

        return new ResponseEntity<WalletResponseModel>(returnedResponse, HttpStatus.ACCEPTED);
    }

    @PutMapping("/withdraw")
    public ResponseEntity<WalletResponseModel> withdraw(@RequestBody WalletRequestModel walletRequestModel) throws InsufficientBalanceException, AuthenticationFailedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        WalletResponseModel returnedResponse = new WalletResponseModel(walletService.withdraw(username, walletRequestModel).getMoney());

        return new ResponseEntity<WalletResponseModel>(returnedResponse, HttpStatus.ACCEPTED);
    }

    @GetMapping("/wallets")
    public ResponseEntity<List<WalletResponseModel>>  getWallets() throws NoWalletPresentException {
        return ResponseEntity.ok(walletService.getAllWallets());
    }
}
