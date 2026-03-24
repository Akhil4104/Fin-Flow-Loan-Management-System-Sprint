package com.finflow.auth_service.service;

import com.finflow.auth_service.dto.AuthResponse;
import com.finflow.auth_service.dto.LoginRequest;
import com.finflow.auth_service.dto.SignupRequest;
import com.finflow.auth_service.entity.User;
import com.finflow.auth_service.repository.UserRepository;
import com.finflow.auth_service.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,BCryptPasswordEncoder passwordEncoder,JwtUtil jwtUtil){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtUtil=jwtUtil;
    }

    public String signup(SignupRequest request){
        String encodedPassword=passwordEncoder.encode(request.getPassword());
        User user=new User(
                request.getName(),
                request.getEmail(),
                encodedPassword,
                request.getRole()
        );
        userRepository.save(user);
        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request){
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new RuntimeException("User not found"));

        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
        return new AuthResponse(token);
    }
}
