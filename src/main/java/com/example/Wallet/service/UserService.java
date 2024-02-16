package com.example.Wallet.service;

import com.example.Wallet.entities.User;
import com.example.Wallet.requestModels.UserRequestModel;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.UserAlreadyExistsException;
import com.example.Wallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(UserRequestModel user) throws UserAlreadyExistsException, InvalidAmountException {

        if(userRepository.findByUserName(user.getUserName()).isPresent())
            throw new UserAlreadyExistsException("Username already exists.");
        User userToSave = new User(user.getUserName(), passwordEncoder.encode(user.getPassword()), new Wallet());
        return userRepository.save(userToSave);
    }


}
