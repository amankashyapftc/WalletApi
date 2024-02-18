package com.example.Wallet.service;

import com.example.Wallet.entities.User;
import com.example.Wallet.exceptions.UserNotFoundException;
import com.example.Wallet.requestModels.UserRequestModel;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.UserAlreadyExistsException;
import com.example.Wallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(UserRequestModel user) throws UserAlreadyExistsException, InvalidAmountException {

        if(userRepository.findByUserName(user.getUserName()).isPresent())
            throw new UserAlreadyExistsException("Username already exists.");
        User userToSave = new User(user.getUserName(), passwordEncoder.encode(user.getPassword()));
        return userRepository.save(userToSave);
    }

    public String delete() throws UserNotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userToDelete = userRepository.findByUserName(username);
        if(userToDelete.isEmpty())
            throw new UserNotFoundException("User could not be found.");

        userRepository.delete(userToDelete.get());
        return "User " + username + " deleted successfully.";
    }

}
