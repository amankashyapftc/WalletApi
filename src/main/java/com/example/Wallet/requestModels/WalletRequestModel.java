package com.example.Wallet.requestModels;


import com.example.Wallet.entities.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WalletRequestModel {
    private Money money;

    public WalletRequestModel(Money money) {
        this.money = money;
    }
}