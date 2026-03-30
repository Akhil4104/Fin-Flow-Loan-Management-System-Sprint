package com.finflow.auth_service.dto;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String mail, String password123) {
        this.email=mail;
        this.password=password123;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
