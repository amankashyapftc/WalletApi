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

import java.util.Arrays;
import java.util.List;

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

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testGetWallets() throws Exception {
        List<Wallet> mockWallets = Arrays.asList(
                new Wallet(1L, new Money(100.0, Currency.INR)),
                new Wallet(2L, new Money(50.0, Currency.INR))
        );
        when(walletService.getAllWallets()).thenReturn(mockWallets);

        mockMvc.perform(MockMvcRequestBuilders.get("/wallets"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                // Verifying the returned JSON content
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].money.amount").value(100.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].money.currency").value("INR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].money.amount").value(50.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].money.currency").value("INR"));
    }
}
