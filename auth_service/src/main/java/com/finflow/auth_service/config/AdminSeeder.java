package com.finflow.auth_service.config;

import com.finflow.auth_service.entity.Role;
import com.finflow.auth_service.entity.User;
import com.finflow.auth_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.name:admin}")
    private String adminName;

    @Value("${app.bootstrap.admin.email:admin123@gmail.com}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password:12345}")
    private String adminPassword;

    public AdminSeeder(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByRole(Role.ADMIN) && userRepository.findByEmail(adminEmail).isEmpty()) {
            log.info("Bootstrap admin skipped because another admin account already exists.");
            return;
        }

        User admin = userRepository.findByEmail(adminEmail)
                .map(existingUser -> {
                    existingUser.setName(adminName);
                    existingUser.setPassword(passwordEncoder.encode(adminPassword));
                    existingUser.setRole(Role.ADMIN);
                    return existingUser;
                })
                .orElseGet(() -> new User(
                        adminName,
                        adminEmail,
                        passwordEncoder.encode(adminPassword),
                        Role.ADMIN
                ));

        userRepository.save(admin);
        log.info("Bootstrap admin is ready for email {}", adminEmail);
    }
}
