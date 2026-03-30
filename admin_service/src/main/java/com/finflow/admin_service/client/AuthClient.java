package com.finflow.admin_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/auth/users")
    List<Object> getAllUsers();

    @GetMapping("/auth/users/{id}")
    Object getUserById(@PathVariable("id") Long id);

    @PutMapping("/auth/users/{id}")
    Object updateUser(@PathVariable("id") Long id, @RequestBody Object request);

    @DeleteMapping("/auth/users/{id}")
    void deleteUser(@PathVariable("id") Long id);
}
