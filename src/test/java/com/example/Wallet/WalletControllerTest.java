package com.example.Wallet;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.entities.WalletRequestModel;
import com.example.Wallet.entities.WalletResponseModel;
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

import java.util.AbstractList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
        WalletRequestModel requestModel = new WalletRequestModel(1L,new Money(0.0, Currency.INR));
        String requestBody = objectMapper.writeValueAsString(requestModel);
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(walletService,times(1)).createWallet();
    }


    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAbleToDepositValid() throws Exception {
        Long walletId = 1L;
        WalletRequestModel requestModel = new WalletRequestModel(1L,new Money(50, Currency.INR));
        String requestBody = objectMapper.writeValueAsString(requestModel);
        mockMvc.perform(MockMvcRequestBuilders.put("/deposit/{id}", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(walletService,times(1)).deposit(anyLong(),any(Money.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAbleToWithdraw() throws Exception {
        Long walletId = 1L;
        WalletRequestModel requestModel = new WalletRequestModel(1L,new Money(100, Currency.INR));
        String requestBody = objectMapper.writeValueAsString(requestModel);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/withdraw/{id}", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(walletService,times(1)).withdraw(anyLong(),any(Money.class));
    }


    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testGetWallets() throws Exception {
        WalletResponseModel firstWallet = new WalletResponseModel();
        WalletResponseModel secondWallet = new WalletResponseModel();
        when(walletService.getAllWallets()).thenReturn(Arrays.asList(firstWallet, secondWallet));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/wallets"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        Wallet[] wallet = new ObjectMapper().readValue(json, Wallet[].class);

        verify(walletService, times(1)).getAllWallets();
        assertEquals(2,wallet.length);
    }
}
