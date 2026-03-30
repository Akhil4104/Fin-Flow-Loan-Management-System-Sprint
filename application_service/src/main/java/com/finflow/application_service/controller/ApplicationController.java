package com.finflow.application_service.controller;

import com.finflow.application_service.dto.ApplicationResponse;
import com.finflow.application_service.dto.CreateApplicationRequest;
import com.finflow.application_service.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/applications")
@Tag(name = "Loan Applications", description = "Endpoints for managing loan applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('APPLICANT')")
    @Operation(summary = "Create Application", description = "Submit a new loan application. Applicants only.")
    public ApplicationResponse create(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateApplicationRequest request) {
        return service.create(userId, request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Application", description = "Update an existing loan application. Admins only.")
    public ApplicationResponse update(
            @PathVariable Long id,
            @RequestBody CreateApplicationRequest request) {
        return service.update(id, request);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('APPLICANT')")
    @Operation(summary = "Submit Application", description = "Submit a drafted loan application for review. Applicants only.")
    public ApplicationResponse submit(@PathVariable Long id){
        return service.submit(id);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('APPLICANT')")
    @Operation(summary = "Get My Applications", description = "Retrieve a list of applications belonging to the current user.")
    public List<ApplicationResponse> myApplications(
            @RequestHeader("X-User-Id") Long userId){
        return service.getByUser(userId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Applications", description = "Retrieve all applications in the system. Admins only.")
    public List<ApplicationResponse> getAllApplications(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {
        return service.getAll(userId, status);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @Operation(summary = "Get Application Details", description = "Fetch details of a specific application by ID. Applicants can access their own application; admins can access any.")
    public ApplicationResponse getApplicationById(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id) {
        return service.getById(id, userId, role);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Application Status", description = "Update the status of an application (e.g., APPROVED, REJECTED). Admins only.")
    public ApplicationResponse updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return service.updateStatus(id, status);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get Application Statistics", description = "Get aggregate statistics about applications. Admins only.")
    public Map<String, Long> getApplicationStats() {
        return service.getStats();
    }

    @GetMapping("/{id}/eligibility")
    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @Operation(summary = "Check Application Eligibility", description = "Check if the loan application is eligible based on financial details.")
    public Map<String, Object> checkEligibility(@PathVariable Long id) {
        return service.checkEligibility(id);
    }
}
