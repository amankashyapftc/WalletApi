package com.example.Wallet;

import com.example.Wallet.controllers.WalletController;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.entities.WalletRequestModel;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
    void testAbleCreateWallet() throws Exception {
        Wallet mockWallet = new Wallet();
        when(walletService.createWallet()).thenReturn(mockWallet);

        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testAbleToDepositValid() throws Exception {
        Long walletId = 1L;
        Wallet mockWallet = new Wallet();
        when(walletService.deposit(walletId, 100)).thenReturn(mockWallet);

        mockMvc.perform(MockMvcRequestBuilders.put("/deposit/{id}", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"money\":100}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testTryToDepositInvalidAmount() throws Exception {
        Long walletId = 1L;

        when(walletService.deposit(walletId,-100.0))
                .thenThrow(new InvalidAmountException("Amount must be positive."));

        mockMvc.perform(MockMvcRequestBuilders.put("/deposit/{id}", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"money\":-100}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    void testAbleToWithdraw() throws Exception {
        Long walletId = 1L;
        Wallet mockWallet = new Wallet();

        when(walletService.withdraw(walletId, 100)).thenReturn(mockWallet);

        mockMvc.perform(MockMvcRequestBuilders.put("/withdraw/{id}", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"money\":100}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testTryToWithdrawNegativeAmountShouldThrowError() throws Exception{
        Long walletId = 1L;
        when(walletService.withdraw(walletId, -100.0))
                .thenThrow(new InvalidAmountException("Amount must be positive."));

        mockMvc.perform(MockMvcRequestBuilders.put("/withdraw/{id}", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"money\":-100}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testTryToWithdrawWhenBalanceIsLessThanAmountShouldThrowError() throws Exception{
        Long walletId = 1L;

        when(walletService.withdraw(walletId, 100))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));

        mockMvc.perform(MockMvcRequestBuilders.put("/withdraw/{id}", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"money\":100}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
