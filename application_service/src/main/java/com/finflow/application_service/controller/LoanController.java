package com.finflow.application_service.controller;

import com.finflow.application_service.service.ApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/applications/loans")
@Tag(name = "Loans", description = "Endpoints for loan metadata")
public class LoanController {

    private final ApplicationService service;

    public LoanController(ApplicationService service) {
        this.service = service;
    }

    @GetMapping("/types")
    @Operation(summary = "Get Loan Types", description = "Retrieve a list of supported loan types.")
    public List<String> getLoanTypes() {
        return service.getLoanTypes();
    }
}
