package com.example.Wallet.service;

import com.example.Wallet.entities.Money;
import com.example.Wallet.entities.User;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.enums.Country;
import com.example.Wallet.enums.Currency;
import com.example.Wallet.exceptions.InsufficientBalanceException;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.UserAlreadyExistsException;
import com.example.Wallet.exceptions.UserNotFoundException;
import com.example.Wallet.repository.UserRepository;
import com.example.Wallet.requestModels.TransactionRequestModel;
import com.example.Wallet.requestModels.UserRequestModel;
import com.example.Wallet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp(){
        openMocks(this);
    }

    @Test
    void testAbleToCreateAUser() throws UserAlreadyExistsException {
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(new User("testUser", "encodedPassword", Country.INDIA));
        UserRequestModel userRequestModel = new UserRequestModel("testUser", "testPassword", Country.INDIA);

        User savedUser = userService.register(userRequestModel);

        assertEquals("testUser", savedUser.getUserName());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertNotNull(savedUser.getWallets());
        verify(userRepository, times(1)).findByUserName("testUser");
        verify(passwordEncoder, times(1)).encode("testPassword");
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testIfUserAlreadyExistsThrowsUserAlreadyExistsException() {
        when(userRepository.findByUserName("existingUser")).thenReturn(Optional.of(new User()));
        UserRequestModel userRequestModel = new UserRequestModel("existingUser", "password", Country.INDIA);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.register(userRequestModel);
        });
        verify(userRepository, times(1)).findByUserName("existingUser");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAbleToDeleteUser() throws UserNotFoundException {
        String username = "testUser";
        User user = new User(username, "password", Country.INDIA);
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String result = userService.delete();

        verify(userRepository, times(1)).findByUserName(username);
        verify(userRepository, times(1)).delete(user);
        assertEquals("User deleted successfully.", result);
    }

    @Test
    void testDeleteUserThrowsUserNotFoundException() {
        String username = "nonExistingUser";
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(UserNotFoundException.class, () -> userService.delete());
        verify(userRepository, times(1)).findByUserName(username);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testAbleToAddWalletToUser() throws UserNotFoundException {
        String username = "testUser";
        User user = new User(1L,username, "password", Country.INDIA, new ArrayList<>());
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        userService.addWallet(1L);

        verify(userRepository, times(1)).findByUserName(username);
        verify(userRepository, times(1)).save(user);
        assertEquals(1, user.getWallets().size() );
    }

    @Test
    void testAbleToAdd2WalletsToUser() throws UserNotFoundException {
        String username = "testUser";
        User user = new User(1L,username, "password", Country.INDIA, new ArrayList<>());
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        userService.addWallet(1L);
        userService.addWallet(1L);

        verify(userRepository, times(2)).findByUserName(username);
        verify(userRepository, times(2)).save(user);
        assertEquals(2, user.getWallets().size() );
    }

    @Test
    void testUserNotFoundException() throws UserNotFoundException {
        String username = "testUser";
        User user = new User(1L,username, "password", Country.INDIA, new ArrayList<>());
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(UserNotFoundException.class,()-> userService.addWallet(2L));
    }

}
