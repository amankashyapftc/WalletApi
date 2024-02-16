package com.example.Wallet.service;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.User;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.exceptions.AuthenticationFailedException;
import com.example.Wallet.responseModels.WalletResponseModel;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.NoWalletPresentException;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.repository.WalletRepository;
import com.example.Wallet.requestModels.WalletRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;


    public WalletResponseModel deposit(String username, WalletRequestModel walletRequestModel) throws AuthenticationFailedException {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new AuthenticationFailedException("Username or password does not match."));

        user.getWallet().deposit(walletRequestModel.getMoney());

        userRepository.save(user);
        return new WalletResponseModel(user.getWallet().getMoney());
    }

    public WalletResponseModel withdraw(String username, WalletRequestModel walletRequestModel) throws InsufficientBalanceException, AuthenticationFailedException {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new AuthenticationFailedException("Username or password does not match."));

        user.getWallet().withdraw(walletRequestModel.getMoney());

        userRepository.save(user);
        return new WalletResponseModel(user.getWallet().getMoney());
    }

    public WalletResponseModel createWallet() throws InvalidAmountException {
        Wallet wallet = new Wallet();
        walletRepository.save(wallet);
        return new WalletResponseModel(wallet.getMoney());
    }

    public Money checkBalance(Long id) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(()->new RuntimeException("No wallet found with this id " + id));
        return wallet.getMoney();
    }

    public List<WalletResponseModel> getAllWallets() throws NoWalletPresentException {
        List<WalletResponseModel> walletList = walletRepository.findAll().stream().map(wallet -> new WalletResponseModel(wallet.getMoney())).toList();
        return walletList;
    }
}

