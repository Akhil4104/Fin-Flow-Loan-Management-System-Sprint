package com.finflow.admin_service.controller;

import com.finflow.admin_service.dto.DecisionRequest;
import com.finflow.admin_service.dto.DecisionResponse;
import com.finflow.admin_service.entity.Decision;
import com.finflow.admin_service.entity.AuditLog;
import com.finflow.admin_service.service.AdminService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Operations", description = "Orchestration endpoints for admins")
public class AdminController {
    private final AdminService service;

    public AdminController(AdminService service){
        this.service=service;
    }

    @PostMapping("/applications/{id}/decision")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Make Loan Decision", description = "Approve or reject a loan application.")
    public DecisionResponse decide(@PathVariable Long id, 
                                   @RequestHeader("X-User-Id") Long adminId, 
                                   @RequestBody DecisionRequest request){
        return service.makeDecision(adminId, id, request);
    }

    @GetMapping("/applications")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Applications", description = "Retrieve a list of applications, optionally filtered by user ID or status.")
    public List<Object> getApplications(@RequestParam(required = false) Long userId,
                                        @RequestParam(required = false) String status) {
        return service.getAllApplications(userId, status);
    }

    @GetMapping("/applications/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get Application by ID", description = "Fetch details of a specific loan application.")
    public Object getApplicationById(@PathVariable Long id) {
        return service.getApplicationById(id);
    }

    @PostMapping("/applications/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign Application", description = "Assign an application to the current admin for review.")
    public Object assignApplication(@PathVariable Long id, @RequestHeader("X-User-Id") Long adminId) {
        return service.assignApplication(adminId, id);
    }

    @PostMapping("/applications/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Review Application", description = "Mark an application as under review.")
    public Object reviewApplication(@PathVariable Long id, @RequestHeader("X-User-Id") Long adminId) {
        return service.reviewApplication(adminId, id);
    }

    @PostMapping("/applications/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Close Application", description = "Close a finalized application.")
    public Object closeApplication(@PathVariable Long id, @RequestHeader("X-User-Id") Long adminId) {
        return service.closeApplication(adminId, id);
    }

    @PutMapping("/documents/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify Document", description = "Mark an uploaded document as verified and legitimate.")
    public Object verifyDocument(@PathVariable Long id, @RequestHeader("X-User-Id") Long adminId) {
        return service.verifyDocument(adminId, id);
    }

    @PutMapping("/documents/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject Document", description = "Mark an uploaded document as invalid or rejected.")
    public Object rejectDocument(@PathVariable Long id, @RequestHeader("X-User-Id") Long adminId) {
        return service.rejectDocument(adminId, id);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Users", description = "Fetch a list of all registered users.")
    public List<Object> getUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get User by ID", description = "Fetch details of a specific user.")
    public Object getUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update User", description = "Update details of a given user.")
    public Object updateUser(@PathVariable Long id, @RequestHeader("X-User-Id") Long adminId, @RequestBody Object request) {
        return service.updateUser(adminId, id, request);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete User", description = "Delete a user account.")
    public void deleteUser(@PathVariable Long id, @RequestHeader("X-User-Id") Long adminId) {
        service.deleteUser(adminId, id);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get Dashboard Stats", description = "Retrieve aggregated key statistics for the admin dashboard.")
    public Map<String, Long> getDashboard() {
        return service.getDashboardStats();
    }



    @GetMapping("/audit")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get Audit Logs", description = "Retrieve all system audit logs.")
    public List<AuditLog> getAuditLogs() {
        return service.getAuditLogs();
    }
}
