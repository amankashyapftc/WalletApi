package com.example.Wallet.service;
import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.User;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.exceptions.AuthenticationFailedException;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.requestModels.WalletRequestModel;
import com.example.Wallet.responseModels.WalletResponseModel;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.NoWalletPresentException;
import com.example.Wallet.repository.WalletRepository;
import com.example.Wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private Wallet wallet;
    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    public void setup(){
        openMocks(this);
    }


    @Test
    public void testCreatingWalletAndCheckingCurrentBalance() throws InvalidAmountException {
        Wallet wallet = new Wallet();
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        Money balance = walletService.checkBalance(1L);
        assertEquals(new Money(0.0, Currency.INR), balance);
    }

    @Test
    public void testDeposit() throws InvalidAmountException, AuthenticationFailedException {
        User user = new User();
        user.setUserName("test");
        user.setWallet(new Wallet());
        when(userRepository.findByUserName("test")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        WalletRequestModel walletRequestModel = new WalletRequestModel(new Money(100,Currency.INR));
        walletService.deposit("test", walletRequestModel);

        verify(userRepository, times(1)).findByUserName("test");
        verify(userRepository, times(1)).save(any());

    }
    @Test
    void testAuthenticationFailedInDeposit() throws InvalidAmountException {
        when(userRepository.findByUserName("nonExistentUser")).thenReturn(Optional.empty());
        WalletRequestModel requestModel = new WalletRequestModel(new Money(50, Currency.INR));

        assertThrows(AuthenticationFailedException.class, () -> {
            walletService.deposit("nonExistentUser", requestModel);
        });
    }
    @Test
    public void testWithdrawWhileHavingSufficientBalance() throws InvalidAmountException, InsufficientBalanceException, AuthenticationFailedException {
        User user = new User("test", "testPassword");

        user.getWallet().deposit(new Money(100,Currency.INR));

        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        WalletRequestModel requestModel = new WalletRequestModel(new Money(50, Currency.INR));

        walletService.withdraw("testUser", requestModel);

        verify(userRepository, times(1)).findByUserName("testUser");
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testAuthenticationFailedWhileWithdraw() throws InvalidAmountException {
        when(userRepository.findByUserName("nonExistentUser")).thenReturn(Optional.empty());
        WalletRequestModel requestModel = new WalletRequestModel(new Money(50, Currency.INR));

        assertThrows(AuthenticationFailedException.class, () -> {
            walletService.withdraw("nonExistentUser", requestModel);
        });
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testWithdrawWhileHavingInsufficientBalance() throws InvalidAmountException {
        User user = new User("test", "testPassword");
        when(userRepository.findByUserName("test")).thenReturn(Optional.of(user));
        WalletRequestModel requestModel = new WalletRequestModel(new Money(50, Currency.INR));

        assertThrows(InsufficientBalanceException.class, () -> {
            walletService.withdraw("test", requestModel);
        });
        verify(userRepository, never()).save(any());
        verify(walletRepository,never()).save(any());
    }

    @Test
    void testOneWalletIsPresentWhenGetAllWallets() throws InvalidAmountException, NoWalletPresentException {
        Wallet wallet = new Wallet();
        when(walletRepository.findAll()).thenReturn(List.of(wallet));

        List<WalletResponseModel> wallets = walletService.getAllWallets();

        assertEquals(1, wallets.size());
        verify(walletRepository, times(1)).findAll();
    }

    @Test
    void testTwoWalletIsPresentWhenGetAllWallets() throws NoWalletPresentException, InvalidAmountException {
        Wallet firstWallet = new Wallet();
        Wallet secondWallet = new Wallet();
        when(walletRepository.findAll()).thenReturn(List.of(firstWallet,secondWallet));

        List<WalletResponseModel> wallets = walletService.getAllWallets();

        assertEquals(2, wallets.size());
        verify(walletRepository, times(1)).findAll();
    }
    @Test
    void testIfWalletHasLessMoneyThanTransactionMoneyItWillThrowInsufficientBalanceException() throws InsufficientBalanceException, InvalidAmountException {
        Money moneyForTransaction = new Money(100, Currency.INR);
        Wallet sendersWallet = new Wallet();
        Wallet receiversWallet = new Wallet();

        assertThrows(InsufficientBalanceException.class,()-> walletService.transact(sendersWallet, receiversWallet, moneyForTransaction));
    }

    @Test
    void testAbleToTransact() throws InsufficientBalanceException, InvalidAmountException {
        Money moneyForTransaction = new Money(100, Currency.INR);
        Wallet sendersWallet = wallet;
        sendersWallet.deposit(new Money(1000, Currency.INR));
        Wallet receiversWallet = wallet;

        walletService.transact(sendersWallet,receiversWallet,moneyForTransaction);

        verify(sendersWallet, times(1)).withdraw(moneyForTransaction);
        verify(receiversWallet, times(1)).deposit(moneyForTransaction);
    }


}
