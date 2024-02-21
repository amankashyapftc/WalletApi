package com.example.Wallet.responseModels;

import com.example.Wallet.entities.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseModel {
    private LocalDateTime timestamp;
    private String sender;
    private String receiver;
    private Money money;

}
