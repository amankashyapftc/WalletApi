package com.example.Wallet.controllers;

import com.example.Wallet.entities.Money;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.requestModels.TransactionRequestModel;
import com.example.Wallet.responseModels.TransactionResponseModel;
import com.example.Wallet.service.TransactionService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        reset(transactionService);
    }

    @Test
    @WithMockUser(username = "sender")
    void testTransactionSuccessful() throws Exception {
        TransactionRequestModel transactionRequestModel = new TransactionRequestModel(1L,"receiver", 2L,new Money(100, Currency.INR));
        String requestJson = objectMapper.writeValueAsString(transactionRequestModel);
        when(transactionService.transact(transactionRequestModel)).thenReturn("Transaction Successful.");

        mockMvc.perform(post("/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Transaction Successful."));
        verify(transactionService, times(1)).transact(transactionRequestModel);
    }

    @Test
    @WithMockUser(username = "sender")
    void testAllTransactionsOfUser() throws Exception {
        TransactionRequestModel transactionRequestModel = new TransactionRequestModel(1L,"receiver",2L, new Money(100, Currency.INR));
        String requestJson = objectMapper.writeValueAsString(transactionRequestModel);
        when(transactionService.allTransactions()).thenReturn(Arrays.asList(new TransactionResponseModel(LocalDateTime.now(),"sender",1L,"receiver",2L, new Money(100, Currency.INR))));

        mockMvc.perform(get("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        verify(transactionService, times(1)).allTransactions();
    }

    @Test
    void testUnauthorizedForAllTransactions() throws Exception {
        TransactionRequestModel transactionRequestModel = new TransactionRequestModel(1L,"receiver",2L, new Money(100, Currency.INR));
        String requestJson = objectMapper.writeValueAsString(transactionRequestModel);
        when(transactionService.allTransactions()).thenReturn(Arrays.asList(new TransactionResponseModel(LocalDateTime.now(),"sender",1L,"receiver",2L, new Money(100, Currency.INR))));

        mockMvc.perform(get("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
        verify(transactionService, times(0)).allTransactions();
    }

    @Test
    @WithMockUser(username = "sender")
    public void testAllTransactionsDateBased() throws Exception {
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 1, 31);
        List<TransactionResponseModel> mockResponse = Arrays.asList(
                new TransactionResponseModel(LocalDateTime.now(),"sender", 1L, "receiver1", 2L, new Money(100, Currency.INR)),
                new TransactionResponseModel(LocalDateTime.now(),"sender", 1L, "receiver2", 3L, new Money(200, Currency.INR))
        );
        when(transactionService.allTransactionsDateBased(startDate, endDate)).thenReturn(mockResponse);

        mockMvc.perform(get("/transactions")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].sender").value("sender"))
                .andExpect(jsonPath("$[0].receiver").value("receiver1"))
                .andExpect(jsonPath("$[0].money.amount").value(100))
                .andExpect(jsonPath("$[1].sender").value("sender"))
                .andExpect(jsonPath("$[1].receiver").value("receiver2"))
                .andExpect(jsonPath("$[1].money.amount").value(200));
        verify(transactionService, times(1)).allTransactionsDateBased(startDate,endDate);
    }

    @Test
    void testUnauthorizedForAllTransactionsDateBased() throws Exception {
        TransactionRequestModel transactionRequestModel = new TransactionRequestModel(1L, "receiver", 2L, new Money(100, Currency.INR));
        String requestJson = objectMapper.writeValueAsString(transactionRequestModel);
        when(transactionService.allTransactionsDateBased(LocalDate.now(), LocalDate.now())).thenReturn(Arrays.asList(new TransactionResponseModel(LocalDateTime.now() ,"sender1", 1L, "receiver1",2L, new Money(100, Currency.INR))));

        mockMvc.perform(get("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
        verify(transactionService, times(0)).allTransactionsDateBased(LocalDate.now(), LocalDate.now());
    }
}
