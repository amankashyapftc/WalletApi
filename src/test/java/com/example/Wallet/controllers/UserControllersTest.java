package com.example.Wallet.controllers;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.User;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Country;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.UserAlreadyExistsException;
import com.example.Wallet.exceptions.UserNotFoundException;
import com.example.Wallet.requestModels.UserRequestModel;
import com.example.Wallet.service.UserService;
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

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllersTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        reset(userService);
    }

    @Test
    void testAbleToCreateUser() throws Exception {
        UserRequestModel userRequestModel = new UserRequestModel("testUser", "testPassword", Country.INDIA);
        User user = new User("testUser", "testPassword", Country.INDIA);

        when(userService.register(userRequestModel)).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestModel)))
                .andExpect(status().isCreated());

    }

    @Test
    void testUserAlreadyExists() throws Exception {
        UserRequestModel userRequestModel = new UserRequestModel("testUser","testPassword", Country.INDIA);

        when(userService.register(any(UserRequestModel.class))).thenThrow(UserAlreadyExistsException.class);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestModel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user")
    void testUserDeleted() throws Exception {
        when(userService.delete()).thenReturn("User Delete SuccessFully.");

        mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("User Delete SuccessFully."));
        verify(userService, times(1)).delete();
    }

    @Test
    @WithMockUser(username = "userNotFound")
    void testUserNotFoundExceptionIsABadRequest() throws Exception {
        String username = "userNotFound";
        String errorMessage = "User "+username+" not be found.";

        when(userService.delete()).thenThrow(new UserNotFoundException(errorMessage));

        mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).delete();
    }

    @Test
    @WithMockUser(username = "user")
    void testWalletAddedToUser() throws Exception {
        User user = new User(1L, "user", "pass",Country.INDIA, Arrays.asList(new Wallet(1L, new Money(0.0,Currency.INR)), new Wallet(2L, new Money(0.0, Currency.INR))));
        when(userService.addWallet(1L)).thenReturn(user);

        mockMvc.perform(put("/users/1/wallet")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wallets.[1]").exists());
        verify(userService, times(1)).addWallet(1L);
    }

    @Test
    @WithMockUser(username = "user")
    void testUserNotFoundWhenWalletAddedIsABadRequest() throws Exception {
        User user = new User(1L, "user", "pass",Country.INDIA, Arrays.asList(new Wallet(1L, new Money(0.0,Currency.INR))));
        when(userService.addWallet(2L)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(put("/users/2/wallet")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).addWallet(2L);
    }

}
