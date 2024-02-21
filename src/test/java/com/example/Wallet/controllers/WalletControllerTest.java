package com.example.Wallet.controllers;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.requestModels.WalletRequestModel;
import com.example.Wallet.responseModels.WalletResponseModel;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAbleCreateWallet() throws Exception {
        WalletRequestModel requestModel = new WalletRequestModel(new Money(0.0, Currency.INR));
        String requestBody = objectMapper.writeValueAsString(requestModel);
        mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(walletService,times(1)).createWallet();
    }


    @Test
    @WithMockUser(username = "user")
    void testAbleToDepositValid() throws Exception {
        WalletRequestModel requestModel = new WalletRequestModel(new Money(100, Currency.INR));
        WalletResponseModel responseModel = new WalletResponseModel(new Money(100, Currency.INR));
        when(walletService.deposit(anyString(), any())).thenReturn(responseModel);

        mockMvc.perform(put("/wallet/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.money.amount").value("100.0"));
        verify(walletService, times(1)).deposit(anyString(),any());
    }

    @Test
    @WithMockUser(username = "user")
    void testAbleToWithdraw() throws Exception {
        WalletRequestModel requestModel = new WalletRequestModel(new Money(50, Currency.INR));
        String requestBody = objectMapper.writeValueAsString(requestModel);
        WalletResponseModel responseModel = new WalletResponseModel(new Money(50, Currency.INR));
        when(walletService.withdraw(anyString(), any())).thenReturn(responseModel);

        mockMvc.perform(put("/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.money.amount").value("50.0"));
        verify(walletService, times(1)).withdraw(anyString(), any(WalletRequestModel.class));
    }


    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testGetWallets() throws Exception {
        WalletResponseModel firstWallet = new WalletResponseModel();
        WalletResponseModel secondWallet = new WalletResponseModel();
        when(walletService.getAllWallets()).thenReturn(Arrays.asList(firstWallet, secondWallet));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/wallet"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        Wallet[] wallet = new ObjectMapper().readValue(json, Wallet[].class);

        verify(walletService, times(1)).getAllWallets();
        assertEquals(2,wallet.length);
    }
}
