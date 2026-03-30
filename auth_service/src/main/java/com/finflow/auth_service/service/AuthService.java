package com.finflow.auth_service.service;

import com.finflow.auth_service.dto.*;

public interface AuthService {
    String signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getProfile(Long userId);
    UserResponse updateProfile(Long userId, ProfileUpdateRequest request);
    String changePassword(Long userId, ChangePasswordRequest request);
    String logout();

    // User management (Admin)
    java.util.List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, com.finflow.auth_service.entity.User request);
    void deleteUser(Long id);
}
