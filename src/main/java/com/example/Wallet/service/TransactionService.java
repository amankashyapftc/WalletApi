package com.example.Wallet.service;


import com.example.Wallet.entities.Transaction;
import com.example.Wallet.entities.User;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.UserNotFoundException;
import com.example.Wallet.repository.TransactionRepository;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.requestModels.TransactionRequestModel;
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
    private WalletService walletService;

    public List<TransactionResponseModel> allTransactions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username).orElseThrow(()-> new UsernameNotFoundException("Username not found."));

        List<Transaction> transactions = transactionRepository.findTransactionsOfUser(user);
        List<TransactionResponseModel> response = transactions.stream().map((transaction -> new TransactionResponseModel(transaction.getTimestamp(), transaction.getSender().getUserName(), transaction.getReceiver().getUserName(), transaction.getMoney()))).collect(Collectors.toList());
        return response;

    }

    public List<TransactionResponseModel> allTransactionsDateBased(LocalDate startDate, LocalDate endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username).orElseThrow(()-> new UsernameNotFoundException("Username not found."));

        List<Transaction> transactions = transactionRepository.findTransactionsOfUserDateBased(user,startDate.atTime(0,0,0), endDate.atTime(23,59,59));
        List<TransactionResponseModel> response = transactions.stream().map((transaction -> new TransactionResponseModel(transaction.getTimestamp(), transaction.getSender().getUserName(), transaction.getReceiver().getUserName(), transaction.getMoney()))).collect(Collectors.toList());

        return response;
    }
    public String transact(TransactionRequestModel requestModel) throws InsufficientBalanceException, InvalidAmountException, UserNotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User "+ username + " not found."));
        User receiver = userRepository.findByUserName(requestModel.getReceiverName()).orElseThrow(() -> new UserNotFoundException("User "+ requestModel.getReceiverName() + " not found."));

        walletService.transact(sender.getWallet(), receiver.getWallet(), requestModel.getMoney());

        userRepository.save(sender);
        userRepository.save(receiver);


        Transaction transaction = new Transaction(LocalDateTime.now(),requestModel.getMoney(), sender, receiver);
        transactionRepository.save(transaction);

        return "Transaction SuccessFull.";
    }
}
