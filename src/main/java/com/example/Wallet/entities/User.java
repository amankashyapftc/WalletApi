package com.example.Wallet.entities;

import com.example.Wallet.enums.Country;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(unique = true)
    private String userName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private Country country;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "USERID")
    private List<Wallet> wallets = new ArrayList<>();

    public User(String userName, String password, Country country) {
        this.userName = userName;
        this.password = password;
        this.country = country;
        this.wallets.add(new Wallet(country));
    }
}
