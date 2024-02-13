package com.example.Wallet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorDetails> insufficientBalanceExceptionHandler(InsufficientBalanceException exception, WebRequest request){
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorDetails> invalidAmountExceptionHandler(InvalidAmountException exception, WebRequest request){
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), exception.getMessage(), request.getDescription(false));

        return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
    }

}
