package com.example.Wallet.requestModels;


import com.example.Wallet.enums.Country;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestModel {

    private String userName;
    private String password;
    private Country country;

}
