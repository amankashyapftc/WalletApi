package com.example.Wallet.service;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Transaction;
import com.example.Wallet.entities.User;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.UserNotFoundException;
import com.example.Wallet.repository.TransactionRepository;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.requestModels.TransactionRequestModel;
import com.example.Wallet.responseModels.TransactionResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
public class TransactionServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp(){
        openMocks(this);
    }

    @Test
    void expectTransactionSuccessful() throws InsufficientBalanceException, InvalidAmountException, UserNotFoundException {
        User sender = new User("sender", "senderPassword");
        User receiver = new User("receiver", "receiverPassword");
        TransactionRequestModel requestModel = spy(new TransactionRequestModel("receiver", new Money(100.0, Currency.INR)));
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(userRepository.findByUserName("receiver")).thenReturn(Optional.of(receiver));

        transactionService.transact(requestModel);

        verify(walletService, times(1)).transact(sender.getWallet(), receiver.getWallet(), requestModel.getMoney());
        verify(userRepository, times(1)).save(sender);
        verify(userRepository, times(1)).save(receiver);
    }

    @Test
    void expectReceiverNotFoundOnTransaction() throws InsufficientBalanceException, InvalidAmountException {
        User sender = new User("sender", "senderPassword");
        User receiver = new User("receiver", "receiverPassword");
        TransactionRequestModel requestModel = new TransactionRequestModel("receiver", new Money(100.0, Currency.INR));
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(userRepository.findByUserName("receiver")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,()-> transactionService.transact(requestModel));
        verify(walletService, times(0)).transact(sender.getWallet(), receiver.getWallet(), requestModel.getMoney());
        verify(userRepository, times(0)).save(sender);
        verify(userRepository, times(0)).save(receiver);
    }

    @Test
    void expectAllTransactions() throws InvalidAmountException {
        User sender = new User("sender","testPassword");
        User receiver = new User("receiver","testPassword");
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Transaction firstTransaction = new Transaction(LocalDateTime.now(),new Money(100, Currency.INR), sender, receiver);
        Transaction secondTransaction = new Transaction(LocalDateTime.now(),new Money(200, Currency.INR), sender, receiver);
        List<Transaction> transactions = Arrays.asList(firstTransaction, secondTransaction);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(transactionRepository.findTransactionsOfUser(sender)).thenReturn(transactions);

        List<TransactionResponseModel> response = transactionService.allTransactions();

        assertEquals(2, response.size());
        verify(userRepository, times(1)).findByUserName("sender");
        verify(transactionRepository, times(1)).findTransactionsOfUser(sender);
    }

    @Test
    void testAllTransactionsDateBased() throws InvalidAmountException {
        User sender = new User("sender", "senderPassword");
        User receiver = new User("receiver", "receiverPassword");
        LocalDateTime startDate = LocalDate.of(2022, 1, 1).atStartOfDay();
        LocalDateTime endDate = LocalDate.of(2022, 1, 31).atTime(23, 59, 59);
        Transaction transaction = new Transaction(LocalDateTime.now(), new Money(100, Currency.INR) , sender, receiver);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(transactionRepository.findTransactionsOfUserDateBased(sender, startDate, endDate)).thenReturn(transactions);

        List<TransactionResponseModel> response = transactionService.allTransactionsDateBased(startDate.toLocalDate(), endDate.toLocalDate());

        assertEquals(1, response.size());
        verify(transactionRepository, times(1)).findTransactionsOfUserDateBased(sender,startDate,endDate);
    }

    @Test
    void testAllTransactionsDateBasedFilterTheData() throws InvalidAmountException {
        User sender = new User("sender", "senderPassword");
        User receiver = new User("receiver", "receiverPassword");
        LocalDateTime startDate = LocalDate.of(2022, 1, 1).atStartOfDay();
        LocalDateTime endDate = LocalDate.of(2022, 1, 31).atTime(23, 59, 59);
        Transaction firstTransaction = new Transaction(LocalDateTime.now(), new Money(100, Currency.INR) , sender, receiver);
        Transaction secondTransaction = new Transaction(LocalDateTime.now().minusDays(2), new Money(100, Currency.INR) , sender, receiver);
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.add(firstTransaction);
        allTransactions.add(secondTransaction);
        List<Transaction> transactionsFiltered = new ArrayList<>();
        transactionsFiltered.add(firstTransaction);
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(transactionRepository.findTransactionsOfUserDateBased(sender, startDate, endDate)).thenReturn(transactionsFiltered);
        when(transactionRepository.findTransactionsOfUser(sender)).thenReturn(allTransactions);

        List<TransactionResponseModel> responseDateBased = transactionService.allTransactionsDateBased(startDate.toLocalDate(), endDate.toLocalDate());
        List<TransactionResponseModel> responseWithoutDate = transactionService.allTransactions();

        assertEquals(1, responseDateBased.size());
        assertEquals(2, responseWithoutDate.size());
        verify(transactionRepository, times(1)).findTransactionsOfUser(sender);
        verify(transactionRepository, times(1)).findTransactionsOfUserDateBased(sender,startDate, endDate);
    }
}
