package com.example.Wallet.controllers;

import com.example.Wallet.exceptions.*;
import com.example.Wallet.requestModels.TransactionRequestModel;
import com.example.Wallet.responseModels.ResponseMessageModel;
import com.example.Wallet.responseModels.TransactionResponseModel;
import com.example.Wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<ResponseMessageModel> transact(@RequestBody TransactionRequestModel transactionRequestModel) throws InsufficientBalanceException, InvalidAmountException, UserNotFoundException, WalletNotFoundException, SameWalletsForTransactionException, CurrencyMismatchException {
        String response = transactionService.transact(transactionRequestModel);
        return new ResponseEntity<>(new ResponseMessageModel(response), HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseModel>> allTransactions(@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate){
        if(startDate != null && endDate != null)
            return new ResponseEntity<>(transactionService.allTransactionsDateBased(startDate,endDate), HttpStatus.OK);
        return new ResponseEntity<>(transactionService.allTransactions(), HttpStatus.OK);
    }
}
