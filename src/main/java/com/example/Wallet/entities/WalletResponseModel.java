package com.example.Wallet.entities;

import lombok.*;

import java.util.AbstractList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseModel {
    private Long id;
    private Money money;
}