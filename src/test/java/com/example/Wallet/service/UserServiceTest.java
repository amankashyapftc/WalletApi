package com.example.Wallet.service;

import com.example.Wallet.entities.User;
import com.example.Wallet.entities.Wallet;
import com.example.Wallet.exceptions.InvalidAmountException;
import com.example.Wallet.exceptions.UserAlreadyExistsException;
import com.example.Wallet.exceptions.UserNotFoundException;
import com.example.Wallet.repository.UserRepository;
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
    void expectUserCreated() throws UserAlreadyExistsException, InvalidAmountException {
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(new User("testUser", "encodedPassword"));
        UserRequestModel userRequestModel = new UserRequestModel("testUser", "testPassword");

        User savedUser = userService.register(userRequestModel);

        assertEquals("testUser", savedUser.getUserName());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertNotNull(savedUser.getWallet());
        verify(userRepository, times(1)).findByUserName("testUser");
        verify(passwordEncoder, times(1)).encode("testPassword");
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void expectUserAlreadyExistsException() {
        when(userRepository.findByUserName("existingUser")).thenReturn(Optional.of(new User()));
        UserRequestModel userRequestModel = new UserRequestModel("existingUser", "password");

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.register(userRequestModel);
        });
        verify(userRepository, times(1)).findByUserName("existingUser");
    }

    @Test
    void expectDeleteUserSuccessfully() throws UserNotFoundException, InvalidAmountException {
        String username = "testUser";
        User user = new User(username, "password");
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));
        String expectedResult = "User " + username + " deleted successfully.";
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String result = userService.delete();

        verify(userRepository, times(1)).findByUserName(username);
        verify(userRepository, times(1)).delete(user);
        assertEquals(expectedResult, result);
    }

    @Test
    void expectDeleteUserThrowsUserNotFoundException() {
        String username = "nonExistingUser";
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(UserNotFoundException.class, () -> userService.delete());
        verify(userRepository, times(1)).findByUserName(username);
        verify(userRepository, never()).delete(any());
    }
}
