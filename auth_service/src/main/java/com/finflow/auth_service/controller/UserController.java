package com.finflow.auth_service.controller;

import com.finflow.auth_service.dto.UserResponse;
import com.finflow.auth_service.entity.User;
import com.finflow.auth_service.service.AuthService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/auth/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users. Admins only.")
    public List<UserResponse> getAllUsers() {
        return authService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user's details by their ID. Admins only.")
    public UserResponse getUser(@PathVariable Long id) {
        return authService.getUserById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user details", description = "Updates the information of an existing user. Admins only.")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody User request) {
        return authService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Removes a user from the system. Admins only.")
    public void deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
    }
}
