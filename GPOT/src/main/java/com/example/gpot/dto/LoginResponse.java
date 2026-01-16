package com.example.gpot.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private String userType;
    private Long userId;
    private String realName;
    private String username;
    private Object userInfo;

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(boolean success, String message, String userType, Long userId, String realName, String username, Object userInfo) {
        this.success = success;
        this.message = message;
        this.userType = userType;
        this.userId = userId;
        this.realName = realName;
        this.username = username;
        this.userInfo = userInfo;
    }
}