package com.example.Wallet;

import com.example.Wallet.controllers.WalletController;
import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAbleCreateWallet() throws Exception {
        Wallet mockWallet = new Wallet();
        when(walletService.createWallet()).thenReturn(mockWallet);

        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"money\":{\"amount\":100,\"currency\":\"USD\"}}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAbleToDepositValid() throws Exception {
        Long walletId = 1L;
        Wallet mockWallet = new Wallet();
        Money money = new Money(100.0, Currency.INR);
        when(walletService.deposit(walletId, money)).thenReturn(mockWallet);

        mockMvc.perform(MockMvcRequestBuilders.put("/deposit/{id}", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"money\":{\"amount\":100,\"currency\":\"USD\"}}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testTryToDepositInvalidAmount() throws Exception {
        Long walletId = 1L;
        assertThrows(InvalidAmountException.class,()->{
            Money money = new Money(-100.0, Currency.INR);
            when(walletService.deposit(walletId, money))
                    .thenThrow(new InvalidAmountException("Money should be positive."));
        });
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAbleToWithdraw() throws Exception {
        Long walletId = 1L;
        Wallet mockWallet = new Wallet();
        Money money = new Money(100.0, Currency.INR);
        when(walletService.withdraw(walletId, money)).thenReturn(mockWallet);

        mockMvc.perform(MockMvcRequestBuilders.put("/withdraw/{id}", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"money\":{\"amount\":100,\"currency\":\"USD\"}}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testTryToWithdrawNegativeAmountShouldThrowError() throws Exception {
        Long walletId = 1L;
        assertThrows(InvalidAmountException.class,()->{
            Money money = new Money(-100.0, Currency.INR);
            when(walletService.deposit(walletId, money))
                    .thenThrow(new InvalidAmountException("Money should be positive."));
        });

    }

    @Test
    void testTryToWithdrawWhenBalanceIsLessThanAmountShouldThrowError() throws Exception {
        Long walletId = 1L;
        Money money = new Money(100.0, Currency.INR);
        when(walletService.withdraw(walletId, money))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));

    }
}
