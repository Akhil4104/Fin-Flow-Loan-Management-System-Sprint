package com.finflow.auth_service.service;

import com.finflow.auth_service.dto.*;
import com.finflow.auth_service.entity.Role;
import com.finflow.auth_service.entity.User;
import com.finflow.auth_service.repository.UserRepository;
import com.finflow.auth_service.security.JwtUtil;
import com.finflow.auth_service.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private SignupRequest signupRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@example.com", "encodedPassword");
        testUser.setId(1L);

        signupRequest = new SignupRequest();
        signupRequest.setName("Test User");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");

        loginRequest = new LoginRequest("test@example.com", "password123");
    }

    @Test
    void signup_ShouldSaveUser() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        String result = authService.signup(signupRequest);

        // Assert
        assertThat(result).isEqualTo("User registered successfully");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(anyLong(), anyString(), anyString())).thenReturn("testToken");

        // Act
        AuthResponse result = authService.login(loginRequest);

        // Assert
        assertThat(result.getToken()).isEqualTo("testToken");
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void getProfile_ShouldReturnUserResponse() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        UserResponse result = authService.getProfile(1L);

        // Assert
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.getName()).isEqualTo(testUser.getName());
    }

    @Test
    void changePassword_WithCorrectOldPassword_ShouldUpdatePassword() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");

        // Act
        String result = authService.changePassword(1L, request);

        // Assert
        assertThat(result).isEqualTo("Password changed successfully");
        verify(userRepository).save(testUser);
        assertThat(testUser.getPassword()).isEqualTo("newEncoded");
    }
}
