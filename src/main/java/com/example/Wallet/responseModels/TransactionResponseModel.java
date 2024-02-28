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
    private Long senderWalletId;
    private String receiver;
    private Long receiverWalletId;
    private Money money;
    private Money serviceFees;

}
