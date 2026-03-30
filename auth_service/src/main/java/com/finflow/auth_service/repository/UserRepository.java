package com.finflow.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.finflow.auth_service.entity.User;
import com.finflow.auth_service.entity.Role;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User>findByEmail(String email);
    boolean existsByRole(Role role);
}
