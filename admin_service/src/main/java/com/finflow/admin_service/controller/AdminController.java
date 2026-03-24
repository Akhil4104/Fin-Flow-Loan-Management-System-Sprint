package com.finflow.admin_service.controller;

import com.finflow.admin_service.dto.DecisionRequest;
import com.finflow.admin_service.dto.DecisionResponse;
import com.finflow.admin_service.entity.Decision;
import com.finflow.admin_service.service.AdminService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService service;

    public AdminController(AdminService service){
        this.service=service;
    }

    @PostMapping("/applications/{id}/decision")
    @PreAuthorize("hasRole('ADMIN')")
    public DecisionResponse decide(@PathVariable Long id, @RequestBody DecisionRequest request){
        return service.makeDecision(id,request);
    }
}
