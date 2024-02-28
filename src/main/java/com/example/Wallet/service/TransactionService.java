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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import proto.ConvertResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.Wallet.constants.Constants.SERVICE_CHARGE_IN_INR;

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
        List<TransactionResponseModel> response = transactions.stream().map((transaction -> new TransactionResponseModel(transaction.getTimestamp(), transaction.getSender().getUserName(), transaction.getSenderWalletId(), transaction.getReceiver().getUserName(), transaction.getReceiverWalletId(), transaction.getMoney(), transaction.getServiceCharge()))).collect(Collectors.toList());

        return response;
    }

    public List<TransactionResponseModel> allTransactionsDateBased(LocalDate startDate, LocalDate endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username).orElseThrow(()-> new UsernameNotFoundException("Username not found."));

        List<Transaction> transactions = transactionRepository.findTransactionsOfUserDateBased(user,startDate.atTime(0,0,0), endDate.atTime(23,59,59));
        List<TransactionResponseModel> response = transactions.stream().map((transaction -> new TransactionResponseModel(transaction.getTimestamp(), transaction.getSender().getUserName(), transaction.getSenderWalletId(), transaction.getReceiver().getUserName(), transaction.getReceiverWalletId(), transaction.getMoney(), transaction.getServiceCharge()))).collect(Collectors.toList());

        return response;
    }


    public String transact(TransactionRequestModel requestModel) throws InsufficientBalanceException, InvalidAmountException, UserNotFoundException, WalletNotFoundException, SameWalletsForTransactionException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User "+ username + " not found."));
        User receiver = userRepository.findByUserName(requestModel.getReceiverName()).orElseThrow(() -> new UserNotFoundException("User "+ requestModel.getReceiverName() + " not found."));
        Wallet senderWallet = walletRepository.findById(requestModel.getSenderWalletId()).orElseThrow(()-> new WalletNotFoundException("Sender Wallet not found."));
        Wallet receiverWallet = walletRepository.findById(requestModel.getReceiverWalletId()).orElseThrow(()-> new WalletNotFoundException("Receiver Wallet not found."));

        if(!sender.getWallets().contains(senderWallet) || !receiver.getWallets().contains(receiverWallet))
            throw new WalletNotFoundException("Wallet is does not match.");
        if(senderWallet.equals(receiverWallet))
            throw new SameWalletsForTransactionException("Wallets are same.");

        CurrencyConverterClient converter = new CurrencyConverterClient();
        ConvertResponse res = converter.convertMoney(requestModel.getMoney(), senderWallet.getMoney().getCurrency(), receiverWallet.getMoney().getCurrency());

        double serviceCharge = res.getServiceCharge().getAmount();

        if(serviceCharge >= requestModel.getMoney().getAmount())
            throw new InvalidAmountException("Amount Less than service charge.");

        senderWallet.withdraw(requestModel.getMoney());

        if(serviceCharge > 0.0)
            requestModel.getMoney().subtract(new Money(serviceCharge, receiverWallet.getMoney().getCurrency()));

        receiverWallet.deposit(requestModel.getMoney());

        userRepository.save(sender);
        userRepository.save(receiver);
        Transaction transaction = new Transaction(LocalDateTime.now(),requestModel.getMoney(), sender, senderWallet.getWalletId(), receiver, receiverWallet.getWalletId(), SERVICE_CHARGE_IN_INR);
        transactionRepository.save(transaction);

        return "Transaction Successful.";
    }


}
