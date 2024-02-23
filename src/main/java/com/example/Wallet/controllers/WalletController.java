package com.example.Wallet.controllers;

import com.example.Wallet.exceptions.*;
import com.example.Wallet.requestModels.WalletRequestModel;
import com.example.Wallet.responseModels.WalletResponseModel;
import com.example.Wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello(){
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }

    @PutMapping("/{wallet_id}/deposit")
    public ResponseEntity<WalletResponseModel> deposit(@PathVariable("wallet_id") Long walletId, @RequestBody WalletRequestModel requestModel) throws InvalidAmountException, AuthenticationFailedException, WalletNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        WalletResponseModel returnedWallet = walletService.deposit(walletId, username, requestModel);

        return new ResponseEntity<>(returnedWallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{wallet_id}/withdraw")
    public ResponseEntity<WalletResponseModel> withdraw(@PathVariable("wallet_id") Long walletId,@RequestBody WalletRequestModel requestModel) throws InsufficientBalanceException, InvalidAmountException, AuthenticationFailedException, WalletNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        WalletResponseModel returnedWallet = walletService.withdraw(walletId, username, requestModel);

        return new ResponseEntity<>(returnedWallet, HttpStatus.ACCEPTED);
    }

    @GetMapping("")
    public ResponseEntity<List<WalletResponseModel>> wallets(){
        List<WalletResponseModel> responseWallets = walletService.getAllWallets();

        return new ResponseEntity<>(responseWallets, HttpStatus.OK);
    }
}
