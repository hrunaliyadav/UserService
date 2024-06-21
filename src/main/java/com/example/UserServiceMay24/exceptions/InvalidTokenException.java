package com.example.UserServiceMay24.exceptions;

import org.springframework.data.jpa.repository.JpaRepository;

public class InvalidTokenException extends Exception{
    public InvalidTokenException(String message) {
        super(message);
    }
}
