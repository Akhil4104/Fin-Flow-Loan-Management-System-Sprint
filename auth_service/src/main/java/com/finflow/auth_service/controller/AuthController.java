package com.finflow.auth_service.controller;

import com.finflow.auth_service.dto.AuthResponse;
import com.finflow.auth_service.dto.ChangePasswordRequest;
import com.finflow.auth_service.dto.LoginRequest;
import com.finflow.auth_service.dto.ProfileUpdateRequest;
import com.finflow.auth_service.dto.SignupRequest;
import com.finflow.auth_service.dto.UserResponse;
import com.finflow.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Creates a new applicant account. Public signup always assigns the APPLICANT role.")
    public String signup(@RequestBody SignupRequest request){
        return authService.signup(request);
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates a user and returns a JWT token.")
    public AuthResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get Profile", description = "Get the current user's profile information.")
    public UserResponse getProfile(@RequestHeader("X-User-Id") Long userId) {
        return authService.getProfile(userId);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update Profile", description = "Update the current user's profile name.")
    public UserResponse updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ProfileUpdateRequest request) {
        return authService.updateProfile(userId, request);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change Password", description = "Update the current user's password.")
    public String changePassword(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ChangePasswordRequest request) {
        return authService.changePassword(userId, request);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout the current user (client should clear token).")
    public String logout() {
        return authService.logout();
    }
}
