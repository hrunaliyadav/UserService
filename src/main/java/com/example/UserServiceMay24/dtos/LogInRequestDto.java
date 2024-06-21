package com.example.UserServiceMay24.dtos;

import lombok.Data;

@Data
public class LogInRequestDto {
    private String email;
    private String password;
}
