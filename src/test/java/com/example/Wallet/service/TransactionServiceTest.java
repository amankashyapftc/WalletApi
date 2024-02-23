package com.example.Wallet.service;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Transaction;
import com.example.Wallet.entities.User;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Country;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.*;
import com.example.Wallet.repository.TransactionRepository;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.repository.WalletRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private PasswordEncoder passwordEncoder;

    @Mock
    private WalletRepository walletRepository;

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
    void expectTransactionSuccessful() throws InsufficientBalanceException, InvalidAmountException, UserNotFoundException, SameWalletsForTransactionException, WalletNotFoundException {
        Wallet senderWallet = spy(new Wallet(1L, new Money(0, Currency.INR)));
        Wallet receiverWallet = spy(new Wallet(2L, new Money(0, Currency.INR)));
        senderWallet.deposit(new Money(100.0,Currency.INR));
        User sender = new User(1L,"sender", "senderPassword", Country.INDIA, Arrays.asList(senderWallet));
        User receiver = new User(2L,"receiver", "receiverPassword", Country.INDIA, Arrays.asList(receiverWallet));
        TransactionRequestModel requestModel = spy(new TransactionRequestModel(1L,"receiver", 2L, new Money(100.0, Currency.INR)));
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(userRepository.findByUserName("receiver")).thenReturn(Optional.of(receiver));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(receiverWallet));

        transactionService.transact(requestModel);

        verify(senderWallet, times(1)).deposit(requestModel.getMoney());
        verify(senderWallet, times(1)).withdraw(requestModel.getMoney());
        verify(receiverWallet, times(1)).deposit(requestModel.getMoney());
        verify(userRepository, times(1)).save(sender);
        verify(userRepository, times(1)).save(receiver);
    }

    @Test
    void expectReceiverNotFoundOnTransaction() throws InsufficientBalanceException, InvalidAmountException {
        Wallet senderWallet = spy(new Wallet(1L, new Money(0, Currency.INR)));
        User sender = new User(1L,"sender", "senderPassword", Country.INDIA, Arrays.asList(senderWallet));
        TransactionRequestModel requestModel = new TransactionRequestModel(1L, "receiver", 2L,new Money(100.0, Currency.INR));
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(userRepository.findByUserName("receiver")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,()-> transactionService.transact(requestModel));

        verify(senderWallet, times(0)).withdraw(requestModel.getMoney());
        verify(userRepository, times(0)).save(sender);
    }

    @Test
    void expectSameWalletsExceptionOnTransaction() throws InsufficientBalanceException, InvalidAmountException {
        Wallet senderWallet = spy(new Wallet(1L, new Money(0, Currency.INR)));
        User sender = new User(1L,"sender", "senderPassword", Country.INDIA, Arrays.asList(senderWallet));
        TransactionRequestModel requestModel = new TransactionRequestModel(1L, "sender", 1L,new Money(100.0, Currency.INR));
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(senderWallet));

        assertThrows(SameWalletsForTransactionException.class,()-> transactionService.transact(requestModel));

        verify(senderWallet, times(0)).deposit(requestModel.getMoney());
        verify(senderWallet, times(0)).withdraw(requestModel.getMoney());
        verify(userRepository, times(0)).save(sender);
    }

    @Test
    void expectSenderWalletNotFoundExceptionOnTransaction() throws InsufficientBalanceException, InvalidAmountException, UserNotFoundException, SameWalletsForTransactionException, WalletNotFoundException {
        Wallet receiverWallet = spy(new Wallet(2L, new Money(0, Currency.INR)));
        User sender = new User(1L,"sender", "senderPassword", Country.INDIA, new ArrayList<>());
        User receiver = new User(2L,"receiver", "receiverPassword", Country.INDIA, Arrays.asList(receiverWallet));
        TransactionRequestModel requestModel = spy(new TransactionRequestModel(1L,"receiver", 2L, new Money(100.0, Currency.INR)));
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(userRepository.findByUserName("receiver")).thenReturn(Optional.of(receiver));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(receiverWallet));

        assertThrows(WalletNotFoundException.class,()-> transactionService.transact(requestModel));
        verify(receiverWallet, times(0)).deposit(requestModel.getMoney());
        verify(userRepository, times(0)).save(sender);
        verify(userRepository, times(0)).save(receiver);
    }

    @Test
    void expectReceiverWalletNotFoundExceptionOnTransaction() throws InsufficientBalanceException, InvalidAmountException, UserNotFoundException, SameWalletsForTransactionException, WalletNotFoundException {
        Wallet senderWallet = spy(new Wallet(2L, new Money(0, Currency.INR)));
        User sender = new User(1L,"sender", "senderPassword", Country.INDIA, Arrays.asList(senderWallet));
        User receiver = new User(2L,"receiver", "receiverPassword", Country.INDIA, new ArrayList<>());
        TransactionRequestModel requestModel = spy(new TransactionRequestModel(1L,"receiver", 2L, new Money(100.0, Currency.INR)));
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(userRepository.findByUserName("receiver")).thenReturn(Optional.of(receiver));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(senderWallet));

        assertThrows(WalletNotFoundException.class,()-> transactionService.transact(requestModel));
        verify(senderWallet, times(0)).deposit(requestModel.getMoney());
        verify(userRepository, times(0)).save(sender);
        verify(userRepository, times(0)).save(receiver);
    }

    @Test
    void expectTransactionSuccessfulForTwoDifferentWallets() throws InsufficientBalanceException, InvalidAmountException, UserNotFoundException, SameWalletsForTransactionException, WalletNotFoundException {
        Wallet firstSenderWallet = spy(new Wallet(1L, new Money(0, Currency.INR)));
        Wallet secondSenderWallet = spy(new Wallet(2L, new Money(0, Currency.INR)));
        firstSenderWallet.deposit(new Money(100.0,Currency.INR));
        User sender = new User(1L,"sender", "senderPassword", Country.INDIA, Arrays.asList(firstSenderWallet, secondSenderWallet));
        TransactionRequestModel requestModel = spy(new TransactionRequestModel(1L,"sender", 2L, new Money(100.0, Currency.INR)));
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(firstSenderWallet));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(secondSenderWallet));

        transactionService.transact(requestModel);

        verify(firstSenderWallet, times(1)).deposit(requestModel.getMoney());
        verify(firstSenderWallet, times(1)).withdraw(requestModel.getMoney());
        verify(secondSenderWallet, times(1)).deposit(requestModel.getMoney());
        verify(userRepository, times(2)).save(sender);
    }

    @Test
    void expectAllTransactions() {
        User sender = new User("sender","testPassword", Country.INDIA);
        User receiver = new User("receiver","testPassword", Country.INDIA);
        when(authentication.getName()).thenReturn("sender");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Transaction firstTransaction = new Transaction(LocalDateTime.now(), new Money(100, Currency.INR), sender, 1L, receiver, 2L);
        Transaction secondTransaction = new Transaction(LocalDateTime.now(),new Money(200, Currency.INR), sender, 1L,receiver, 2L);
        List<Transaction> transactions = Arrays.asList(firstTransaction, secondTransaction);
        when(userRepository.findByUserName("sender")).thenReturn(Optional.of(sender));
        when(transactionRepository.findTransactionsOfUser(sender)).thenReturn(transactions);

        List<TransactionResponseModel> response = transactionService.allTransactions();

        assertEquals(2, response.size());
        verify(userRepository, times(1)).findByUserName("sender");
        verify(transactionRepository, times(1)).findTransactionsOfUser(sender);
    }

    @Test
    void expectAllTransactionsDateBased() {
        User sender = new User("sender", "senderPassword", Country.INDIA);
        User receiver = new User("receiver", "receiverPassword", Country.INDIA);
        LocalDateTime startDate = LocalDate.of(2022, 1, 1).atStartOfDay();
        LocalDateTime endDate = LocalDate.of(2022, 1, 31).atTime(23, 59, 59);
        Transaction transaction = new Transaction(LocalDateTime.now(), new Money(100, Currency.INR) , sender, 1L, receiver, 2L);
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
    void expectAllTransactionsDateBasedDifferentFromAllTransaction() {
        User sender = new User("sender", "senderPassword", Country.INDIA);
        User receiver = new User("receiver", "receiverPassword", Country.INDIA);
        LocalDateTime startDate = LocalDate.of(2022, 1, 1).atStartOfDay();
        LocalDateTime endDate = LocalDate.of(2022, 1, 31).atTime(23, 59, 59);
        Transaction firstTransaction = new Transaction(LocalDateTime.now(), new Money(100, Currency.INR) , sender, 1L, receiver, 2L);
        Transaction secondTransaction = new Transaction(LocalDateTime.now().minusDays(2), new Money(100, Currency.INR) , sender, 1L, receiver, 2L);
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
