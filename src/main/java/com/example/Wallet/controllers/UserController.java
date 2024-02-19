package com.example.Wallet.controllers;


import com.example.Wallet.entities.User;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.UserNotFoundException;
import com.example.Wallet.requestModels.TransactionRequestModel;
import com.example.Wallet.requestModels.UserRequestModel;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.UserAlreadyExistsException;
import com.example.Wallet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("")
    public ResponseEntity<User> register(@RequestBody UserRequestModel user) throws UserAlreadyExistsException, InvalidAmountException {
        User returnedUser = userService.register(user);
        return new ResponseEntity<User>(returnedUser, HttpStatus.CREATED);
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteUser() throws UserNotFoundException {
        String response = userService.delete();
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PutMapping("/transact")
    public ResponseEntity<String> transact(@RequestBody TransactionRequestModel transactionRequestModel) throws InsufficientBalanceException, InvalidAmountException, InsufficientBalanceException {
        String response = userService.transact(transactionRequestModel);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}