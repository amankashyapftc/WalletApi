package com.example.Wallet.controllers;

import com.example.Wallet.entities.Money;
import com.example.Wallet.requestModels.WalletRequestModel;
import com.example.Wallet.responseModels.WalletResponseModel;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        reset(walletService);
    }

    @Test
    @WithMockUser(username = "user")
    void testAmountDepositedSuccessfully() throws Exception {
        WalletRequestModel requestModel = new WalletRequestModel(new Money(100, Currency.INR));
        WalletResponseModel responseModel = new WalletResponseModel(1L,new Money(100, Currency.INR));
        when(walletService.deposit(anyLong(), anyString(), any())).thenReturn(responseModel);

        mockMvc.perform(put("/wallet/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.money.amount").value("100.0"));
        verify(walletService, times(1)).deposit(anyLong(), anyString(),any());
    }

    @Test
    void testUnauthorizedOnDepositIsAIsUnAuthorisedRequest() throws Exception {
        WalletRequestModel requestModel = new WalletRequestModel(new Money(100, Currency.INR));
        WalletResponseModel responseModel = new WalletResponseModel(1L, new Money(100, Currency.INR));
        when(walletService.deposit(anyLong(), anyString(), any())).thenReturn(responseModel);

        mockMvc.perform(put("/wallets/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isUnauthorized());
        verify(walletService, never()).deposit(anyLong(), anyString(),any());
    }

    @Test
    @WithMockUser(username = "user")
    void testWithdrawalSuccessful() throws Exception {
        WalletRequestModel requestModel = new WalletRequestModel(new Money(50, Currency.INR));
        String requestBody = objectMapper.writeValueAsString(requestModel);
        WalletResponseModel responseModel = new WalletResponseModel(1L, new Money(50, Currency.INR));
        when(walletService.withdraw(anyLong(), anyString(), any())).thenReturn(responseModel);

        mockMvc.perform(put("/wallet/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.money.amount").value("50.0"));
        verify(walletService, times(1)).withdraw(anyLong(), anyString(), any(WalletRequestModel.class));
    }

    @Test
    void testUnauthorizedOnWithdrawalIsAIsUnAuthorisedRequest() throws Exception {
        WalletRequestModel requestModel = new WalletRequestModel(new Money(50, Currency.INR));
        String requestBody = objectMapper.writeValueAsString(requestModel);

        mockMvc.perform(put("/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
        verify(walletService, never()).withdraw(anyLong(), anyString(), any(WalletRequestModel.class));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void testWalletListForUser() throws Exception {
        WalletResponseModel firstWallet = new WalletResponseModel();
        WalletResponseModel secondWallet = new WalletResponseModel();
        when(walletService.getAllWallets()).thenReturn(Arrays.asList(firstWallet, secondWallet));

        MvcResult mockResult = mockMvc.perform(get("/wallet")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        String responseContent = mockResult.getResponse().getContentAsString();
        WalletResponseModel[] walletResponse = objectMapper.readValue(responseContent, WalletResponseModel[].class);

        verify(walletService, times(1)).getAllWallets();
        assertEquals(2, walletResponse.length);
    }

    @Test
    void testUnauthorizedForWalletListIsAIsUnAuthorisedRequest() throws Exception {
        WalletResponseModel firstWallet = new WalletResponseModel();
        WalletResponseModel secondWallet = new WalletResponseModel();
        when(walletService.getAllWallets()).thenReturn(Arrays.asList(firstWallet, secondWallet));

        mockMvc.perform(get("/wallet")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(walletService, never()).getAllWallets();
    }
}
