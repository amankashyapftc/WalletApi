package com.example.Wallet.requestModels;

import com.example.Wallet.entities.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestModel {

    private String receiverName;
    private Money money;
}