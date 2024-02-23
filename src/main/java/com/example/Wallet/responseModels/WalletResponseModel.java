package com.example.Wallet.responseModels;

import com.example.Wallet.entities.Money;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseModel {
    private Long walletId;
    private Money money;
}