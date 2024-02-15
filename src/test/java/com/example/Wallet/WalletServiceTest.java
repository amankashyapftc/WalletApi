package com.example.Wallet;
import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.NoWalletPresentException;
import com.example.Wallet.repository.WalletRepository;
import com.example.Wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @InjectMocks
    private WalletService walletService;

    @Test
    public void testCreatingWalletAndCheckingCurrentBalance() throws InvalidAmountException {
        Wallet wallet = new Wallet();
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        Money balance = walletService.checkBalance(1L);
        assertEquals(new Money(0.0, Currency.INR), balance);
    }

    @Test
    public void testDeposit() throws InvalidAmountException {
        Wallet wallet = spy(new Wallet());
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        Money depositAmount = new Money(20.0, Currency.INR);
        walletService.deposit(1L,depositAmount);

        verify(walletRepository,times(1)).save(wallet);
        verify(wallet,times(1)).deposit(any());

        assertEquals(new Money(20.0, Currency.INR), wallet.getMoney());
    }

    @Test
    public void testWithdrawWhileHavingSufficientBalance() throws InvalidAmountException, InsufficientBalanceException {
        Wallet wallet = spy(new Wallet());
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        Money depositAmount = new Money(20.0,Currency.INR);
        Money withdrawAmount = new Money(10.0,Currency.INR);

        walletService.deposit(1L,depositAmount);
        walletService.withdraw(1L,withdrawAmount);

        verify(walletRepository,times(2)).save(wallet);
        verify(wallet,times(1)).deposit(any());
        verify(wallet,times(1)).withdraw(any());
    }

    @Test
    public void testWithdrawWhileHavingInsufficientBalance() throws InvalidAmountException {
        Wallet wallet = spy(new Wallet());
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        Money depositAmount = new Money(20.0,Currency.INR);
        walletService.deposit(1L,depositAmount);
        assertEquals(new Money(20.0, Currency.INR), wallet.getMoney());

        verify(walletRepository,times(1)).save(wallet);

        Money withdrawAmount = new Money(30.0,Currency.INR);
        assertThrows(InsufficientBalanceException.class, ()-> walletService.withdraw(1L,withdrawAmount));
        assertEquals(new Money(20.0, Currency.INR),wallet.getMoney());
    }

    @Test
    void testGetAllWalletsWhenNotEmpty() throws InvalidAmountException, NoWalletPresentException {
        List<Wallet> mockWalletList = new ArrayList<>();
        mockWalletList.add(new Wallet(1L, new Money(100.0, Currency.INR)));
        mockWalletList.add(new Wallet(2L, new Money(50.0, Currency.INR)));
        when(walletRepository.findAll()).thenReturn(mockWalletList);

        List<Wallet> result = walletService.getAllWallets();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(new Money(100.0, Currency.INR), result.get(0).getMoney());
        assertEquals(2L, result.get(1).getId());
        assertEquals(new Money(50.0, Currency.INR), result.get(1).getMoney());
    }

    @Test
    void testGetAllWalletsWhenEmpty() {
        when(walletRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(RuntimeException.class, () -> walletService.getAllWallets());
    }

}
