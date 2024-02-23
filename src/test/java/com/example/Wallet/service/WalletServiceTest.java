package com.example.Wallet.service;


import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.User;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Country;
import com.example.Wallet.exceptions.*;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.requestModels.WalletRequestModel;
import com.example.Wallet.responseModels.WalletResponseModel;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
public class WalletServiceTest {

    @MockBean
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
    void testAbleToCreateWallet() {
        Wallet wallet = new Wallet();
        when(walletRepository.save(any())).thenReturn(wallet);

        Wallet createdWallet = walletService.create(new Wallet());

        assertNotNull(createdWallet);
        verify(walletRepository, times(1)).save(any());
    }

    @Test
    void testAmountDepositedWithValidAmount() throws Exception {
        User user = spy(new User("testUser", "testPassword", Country.INDIA));
        Wallet wallet = new Wallet(1L, new Money(0.0, Currency.INR));
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(user.getWallets()).thenReturn(List.of(wallet));
        WalletRequestModel requestModel = new WalletRequestModel(new Money(100,Currency.INR));

        walletService.deposit(1L, "testUser", requestModel);

        verify(walletRepository, times(1)).findById(1L);
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void testAuthenticationFailedInDeposit() {
        when(userRepository.findByUserName("nonExistentUser")).thenReturn(Optional.empty());
        WalletRequestModel requestModel = new WalletRequestModel(new Money(50, Currency.INR));

        assertThrows(AuthenticationFailedException.class, () -> {
            walletService.deposit(anyLong(), "nonExistentUser", requestModel);
        });
    }

    @Test
    void testAmountWithdrawn() throws Exception {
        Wallet wallet = new Wallet(1L, new Money(0.0,Currency.INR));
        wallet.deposit(new Money(100, Currency.INR));
        User user = spy(new User(1L,"testUser", "testPassword", Country.INDIA, List.of(wallet)));
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(user.getWallets()).thenReturn(List.of(wallet));
        WalletRequestModel requestModel = new WalletRequestModel(new Money(50, Currency.INR));

        WalletResponseModel returnedWallet = walletService.withdraw(1L, "testUser", requestModel);

        assertEquals(50, returnedWallet.getMoney().getAmount());
        verify(walletRepository, times(1)).findById(1L);
        verify(walletRepository, times(1)).save(any());
    }

    @Test
    void testInsufficientBalanceException() throws AuthenticationFailedException, InvalidAmountException {
        Wallet wallet = new Wallet(1L,new Money(0.0,Currency.INR));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        User user = spy(new User("testUser", "testPassword", Country.INDIA));
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(user.getWallets()).thenReturn(List.of(wallet));
        WalletRequestModel requestModel = new WalletRequestModel(new Money(50, Currency.INR));

        assertThrows(InsufficientBalanceException.class, () -> {
            walletService.withdraw(1L, "testUser", requestModel);
        });
        verify(userRepository, never()).save(any());
        verify(walletRepository,never()).save(any());
    }

    @Test
    void testAbleToGetWalletList() {
        Wallet wallet = new Wallet();
        when(walletRepository.findAll()).thenReturn(List.of(wallet));

        List<WalletResponseModel> wallets = walletService.getAllWallets();

        assertEquals(1, wallets.size());
        verify(walletRepository, times(1)).findAll();
    }

    @Test
    void testAbleToGetWalletListOfSize2() {
        Wallet firstWallet = new Wallet();
        Wallet secondWallet = new Wallet();
        when(walletRepository.findAll()).thenReturn(Arrays.asList(firstWallet,secondWallet));

        List<WalletResponseModel> wallets = walletService.getAllWallets();

        assertEquals(2, wallets.size());
        verify(walletRepository, times(1)).findAll();
    }

    @Test
    void testAuthenticationFailed() {
        when(userRepository.findByUserName("nonExistentUser")).thenReturn(Optional.empty());
        WalletRequestModel requestModel = new WalletRequestModel(new Money(50, Currency.INR));

        assertThrows(AuthenticationFailedException.class, () -> {
            walletService.withdraw(anyLong(), "nonExistentUser", requestModel);
        });
        verify(userRepository, never()).save(any());
    }

}
