package com.example.Wallet.controllers;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.User;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.UserAlreadyExistsException;
import com.example.Wallet.requestModels.TransactionRequestModel;
import com.example.Wallet.requestModels.UserRequestModel;
import com.example.Wallet.service.UserService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllersTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @MockBean
    private WalletService walletService;



    @BeforeEach
    void setUp() {
        reset(userService);
    }

    @Test
    void testUserCreatedSuccessFully() throws Exception {
        UserRequestModel userRequestModel = new UserRequestModel("testUser", "testPassword");
        User user = new User("testUser", "testPassword");

        when(userService.register(userRequestModel)).thenReturn(user);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestModel)))
                .andExpect(status().isCreated());
        verify(userService, times(1)).register(any());
    }

    @Test
    void testRegisterAgainWithSameUserGivesBadRequestAndUserServiceNeverCalled() throws Exception {
        UserRequestModel userRequestModel = new UserRequestModel("testUser","testPassword");
        User user = new User("testUser", "testPassword");

        when(userService.register(any(UserRequestModel.class))).thenThrow(UserAlreadyExistsException.class);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestModel)))
                .andExpect(status().isBadRequest());

    }
    @Test
    @WithMockUser(username = "sender")
    void testTransactEndpoint() throws Exception {
        TransactionRequestModel transactionRequestModel = new TransactionRequestModel("sender", new Money(100, Currency.INR));
        String requestJson = objectMapper.writeValueAsString(transactionRequestModel);
        when(userService.transact(transactionRequestModel)).thenReturn("Transaction SuccessFull.");

        mockMvc.perform(put("/user/transact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isAccepted());

    }

}
