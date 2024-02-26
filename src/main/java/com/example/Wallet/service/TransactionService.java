package com.example.Wallet.service;


import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Transaction;
import com.example.Wallet.entities.User;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.*;
import com.example.Wallet.grpcClient.CurrencyConverterClient;
import com.example.Wallet.repository.TransactionRepository;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.repository.WalletRepository;
import com.example.Wallet.requestModels.TransactionRequestModel;
import com.example.Wallet.responseModels.TransactionResponseModel;
import currencyconversion.ConvertResponse;
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

    private final CurrencyConverterClient conversionClient;

    public TransactionService(CurrencyConverterClient conversionClient) {
        this.conversionClient = conversionClient;
    }


    public List<TransactionResponseModel> allTransactions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username).orElseThrow(()-> new UsernameNotFoundException("Username not found."));

        List<Transaction> transactions = transactionRepository.findTransactionsOfUser(user);
        List<TransactionResponseModel> response = transactions.stream().map((transaction -> new TransactionResponseModel(transaction.getTimestamp(), transaction.getSender().getUserName(), transaction.getSenderWalletId(), transaction.getReceiver().getUserName(), transaction.getReceiverWalletId(), transaction.getMoney(),transaction.getServiceFees()))).collect(Collectors.toList());

        return response;
    }

    public List<TransactionResponseModel> allTransactionsDateBased(LocalDate startDate, LocalDate endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username).orElseThrow(()-> new UsernameNotFoundException("Username not found."));

        List<Transaction> transactions = transactionRepository.findTransactionsOfUserDateBased(user,startDate.atTime(0,0,0), endDate.atTime(23,59,59));
        List<TransactionResponseModel> response = transactions.stream().map((transaction -> new TransactionResponseModel(transaction.getTimestamp(), transaction.getSender().getUserName(), transaction.getSenderWalletId(), transaction.getReceiver().getUserName(), transaction.getReceiverWalletId(), transaction.getMoney(), transaction.getServiceFees()))).collect(Collectors.toList());

        return response;
    }

    public String transact(TransactionRequestModel requestModel) throws InsufficientBalanceException, InvalidAmountException, UserNotFoundException, WalletNotFoundException, SameWalletsForTransactionException, CurrencyMismatchException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User "+ username + " not found."));
        User receiver = userRepository.findByUserName(requestModel.getReceiverName()).orElseThrow(() -> new UserNotFoundException("User "+ requestModel.getReceiverName() + " not found."));
        Wallet senderWallet = walletRepository.findById(requestModel.getSenderWalletId()).orElseThrow(()-> new WalletNotFoundException("Sender Wallet Id not Found."));

        if(!senderWallet.getMoney().getCurrency().equals(requestModel.getMoney().getCurrency()))
            throw new CurrencyMismatchException("Currency is not matching.");

        Wallet receiverWallet = walletRepository.findById(requestModel.getReceiverWalletId()).orElseThrow(()-> new WalletNotFoundException("Receiver Wallet Id Not found."));

        if(!sender.getWallets().contains(senderWallet) || !receiver.getWallets().contains(receiverWallet))
            throw new WalletNotFoundException("Wallet Id Does Not match.");
        if(senderWallet.equals(receiverWallet))
            throw new SameWalletsForTransactionException("Wallets Same in Transaction.");

        senderWallet.withdraw(requestModel.getMoney());

        Double serviceFees = currencyConversion(senderWallet,receiverWallet,requestModel.getMoney());

        userRepository.save(sender);
        userRepository.save(receiver);
        Transaction transaction = new Transaction(LocalDateTime.now(),requestModel.getMoney(), sender, senderWallet.getWalletId(), receiver, receiverWallet.getWalletId(),serviceFees);
        transactionRepository.save(transaction);

        return "Transaction Successful.";
    }

    public Double currencyConversion(Wallet senderWallet , Wallet receiverWallet, Money transferAmount) throws InvalidAmountException {

        if(!senderWallet.getMoney().getCurrency().equals(receiverWallet.getMoney().getCurrency())){
            ConvertResponse response = conversionClient.convertCurrency(transferAmount.getCurrency().toString(), receiverWallet.getMoney().getCurrency().toString(),
                    transferAmount.getAmount());
            Money convertedAmount = new Money(response.getConvertedAmount(), Currency.valueOf(response.getCurrency()));
            receiverWallet.deposit(convertedAmount);
            return response.getServiceFee();
        }else{
            receiverWallet.deposit(transferAmount);
        }
        return 0.0;
    }
}
