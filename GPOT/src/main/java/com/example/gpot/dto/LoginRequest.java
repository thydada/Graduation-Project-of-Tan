package com.example.gpot.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String userType;
    private String username;
    private String password;
}