package com.finflow.application_service.controller;

import com.finflow.application_service.dto.ApplicationResponse;
import com.finflow.application_service.dto.CreateApplicationRequest;
import com.finflow.application_service.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApplicationResponse create(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateApplicationRequest request) {
        return service.create(userId, request);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApplicationResponse update(
            @PathVariable Long id,
            @RequestBody CreateApplicationRequest request) {
        return service.update(id, request);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApplicationResponse submit(@PathVariable Long id){
        return service.submit(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<ApplicationResponse> myApplications(
            @RequestHeader("X-User-Id") Long userId){
        return service.getByUser(userId);
    }
}