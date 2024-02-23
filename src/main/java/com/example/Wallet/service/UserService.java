package com.example.Wallet.service;

import com.example.Wallet.entities.User;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.UserNotFoundException;
import com.example.Wallet.requestModels.TransactionRequestModel;
import com.example.Wallet.requestModels.UserRequestModel;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.UserAlreadyExistsException;
import com.example.Wallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(UserRequestModel user) throws UserAlreadyExistsException {
        if(userRepository.findByUserName(user.getUserName()).isPresent())
            throw new UserAlreadyExistsException("User Already Exists.");
        User userToSave = new User(user.getUserName(), passwordEncoder.encode(user.getPassword()), user.getCountry());
        return userRepository.save(userToSave);
    }

    public String delete() throws UserNotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userToDelete = userRepository.findByUserName(username);
        if(userToDelete.isEmpty())
            throw new UserNotFoundException("User could not be found.");

        userRepository.delete(userToDelete.get());
        return "User deleted successfully.";
    }

    public User addWallet(Long userId) throws UserNotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username).orElseThrow(() -> new UserNotFoundException("User Not Found."));
        if(user.getUserId() != userId)
            throw new UserNotFoundException("Invalid User Id.");

        user.getWallets().add(new Wallet(user.getCountry()));
        return userRepository.save(user);
    }


}
