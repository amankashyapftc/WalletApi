package com.example.Wallet.service;


import com.example.Wallet.entities.Transaction;
import com.example.Wallet.entities.User;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.exceptions.*;
import com.example.Wallet.repository.TransactionRepository;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.repository.WalletRepository;
import com.example.Wallet.requestModels.TransactionRequestModel;
import com.example.Wallet.requestModels.WalletRequestModel;
import com.example.Wallet.responseModels.TransactionResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletService walletService;


    public List<TransactionResponseModel> allTransactions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username).orElseThrow(()-> new UsernameNotFoundException("Username not found."));

        List<Transaction> transactions = transactionRepository.findTransactionsOfUser(user);
        List<TransactionResponseModel> response = transactions.stream().map((transaction -> new TransactionResponseModel(transaction.getTimestamp(), transaction.getSender().getUserName(), transaction.getSenderWalletId(), transaction.getReceiver().getUserName(), transaction.getReceiverWalletId(), transaction.getMoney()))).collect(Collectors.toList());

        return response;
    }

    public List<TransactionResponseModel> allTransactionsDateBased(LocalDate startDate, LocalDate endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username).orElseThrow(()-> new UsernameNotFoundException("Username not found."));

        List<Transaction> transactions = transactionRepository.findTransactionsOfUserDateBased(user,startDate.atTime(0,0,0), endDate.atTime(23,59,59));
        List<TransactionResponseModel> response = transactions.stream().map((transaction -> new TransactionResponseModel(transaction.getTimestamp(), transaction.getSender().getUserName(), transaction.getSenderWalletId(), transaction.getReceiver().getUserName(), transaction.getReceiverWalletId(), transaction.getMoney()))).collect(Collectors.toList());

        return response;
    }

    public String transact(TransactionRequestModel requestModel) throws InsufficientBalanceException, InvalidAmountException, UserNotFoundException, WalletNotFoundException, SameWalletsForTransactionException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User "+ username + " not found."));
        User receiver = userRepository.findByUserName(requestModel.getReceiverName()).orElseThrow(() -> new UserNotFoundException("User "+ requestModel.getReceiverName() + " not found."));
        Wallet senderWallet = walletRepository.findById(requestModel.getSenderWalletId()).orElseThrow(()-> new WalletNotFoundException("Sender Wallet Id not Found."));
        Wallet receiverWallet = walletRepository.findById(requestModel.getReceiverWalletId()).orElseThrow(()-> new WalletNotFoundException("Receiver Wallet Id Not found."));

        if(!sender.getWallets().contains(senderWallet) || !receiver.getWallets().contains(receiverWallet))
            throw new WalletNotFoundException("Wallet Id Does Not match.");
        if(senderWallet.equals(receiverWallet))
            throw new SameWalletsForTransactionException("Wallets Same in Transaction.");

        senderWallet.withdraw(requestModel.getMoney());
        receiverWallet.deposit(requestModel.getMoney());

        userRepository.save(sender);
        userRepository.save(receiver);
        Transaction transaction = new Transaction(LocalDateTime.now(),requestModel.getMoney(), sender, senderWallet.getWalletId(), receiver, receiverWallet.getWalletId());
        transactionRepository.save(transaction);

        return "Transaction Successful.";
    }
}
