package com.example.Wallet.service;

import com.example.Wallet.entities.User;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.exceptions.*;
import com.example.Wallet.responseModels.WalletResponseModel;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.repository.WalletRepository;
import com.example.Wallet.requestModels.WalletRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    public Wallet create(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    public List<WalletResponseModel> getAllWallets() {
        List<Wallet> wallets = walletRepository.findAll();
        List<WalletResponseModel> response = new ArrayList<>();
        for(Wallet wallet : wallets){
            response.add(new WalletResponseModel(wallet.getWalletId(), wallet.getMoney()));
        }
        return response;
    }

    public WalletResponseModel deposit(Long walletId, String username, WalletRequestModel requestModel) throws InvalidAmountException, AuthenticationFailedException, WalletNotFoundException {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new AuthenticationFailedException("Username or password does not match."));
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet id does not match."));
        if(!user.getWallets().contains(wallet))
            throw new AuthenticationFailedException("Wallet Id does not match.");

        wallet.deposit(requestModel.getMoney());

        walletRepository.save(wallet);
        return new WalletResponseModel(walletId, wallet.getMoney());
    }

    public WalletResponseModel withdraw(Long walletId, String username, WalletRequestModel requestModel) throws InsufficientBalanceException, InvalidAmountException, AuthenticationFailedException, WalletNotFoundException {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new AuthenticationFailedException("Username or password does not match."));
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet Id does not match."));
        if(!user.getWallets().contains(wallet))
            throw new AuthenticationFailedException("Wallet Id does not match.");

        wallet.withdraw(requestModel.getMoney());

        walletRepository.save(wallet);
        return new WalletResponseModel(walletId, wallet.getMoney());
    }

}

