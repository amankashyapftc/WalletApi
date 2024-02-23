package com.example.Wallet.controllers;


import com.example.Wallet.entities.User;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.UserNotFoundException;
import com.example.Wallet.requestModels.TransactionRequestModel;
import com.example.Wallet.requestModels.UserRequestModel;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.UserAlreadyExistsException;
import com.example.Wallet.responseModels.ResponseMessageModel;
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

    @PostMapping("")
    public ResponseEntity<User> registerUser(@RequestBody UserRequestModel user) throws UserAlreadyExistsException {
        User returnedUser = userService.register(user);
        return new ResponseEntity<>(returnedUser, HttpStatus.CREATED);
    }

    @DeleteMapping("")
    public ResponseEntity<ResponseMessageModel> deleteUser() throws UserNotFoundException {
        String response = userService.delete();
        return new ResponseEntity<>(new ResponseMessageModel(response), HttpStatus.ACCEPTED);
    }

    @PutMapping("/{user_id}/wallet")
    public ResponseEntity<User> addWallet(@PathVariable("user_id") Long userId) throws UserNotFoundException {
        User returnedUser = userService.addWallet(userId);
        return new ResponseEntity<>(returnedUser, HttpStatus.CREATED);
    }

}