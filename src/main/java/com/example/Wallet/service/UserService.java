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

    public String transact(TransactionRequestModel requestModel) throws InsufficientBalanceException, InvalidAmountException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User "+ username + " not found."));
        User receiver = userRepository.findByUserName(requestModel.getReceiverName()).orElseThrow(() -> new UsernameNotFoundException("User "+ requestModel.getReceiverName() + " not found."));

        walletService.transact(sender.getWallet(), receiver.getWallet(), requestModel.getMoney());

        userRepository.save(sender);
        userRepository.save(receiver);

        return "Transaction SuccessFull.";
    }

}
