package com.finflow.application_service.service.impl;

import com.finflow.application_service.dto.ApplicationResponse;
import com.finflow.application_service.dto.CreateApplicationRequest;
import com.finflow.application_service.entity.ApplicationStatus;
import com.finflow.application_service.entity.LoanApplication;
import com.finflow.application_service.repository.LoanApplicationRepository;
import com.finflow.application_service.service.ApplicationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    private final LoanApplicationRepository repository;

    public ApplicationServiceImpl(LoanApplicationRepository repository){
        this.repository=repository;
    }

    @Override
    public ApplicationResponse create(Long userId, CreateApplicationRequest request){
        LoanApplication app = new LoanApplication(
                null, 
                userId, 
                request.getAmount(), 
                request.getLoanType(), 
                request.getTenure(), 
                request.getIncome(), 
                request.getEmploymentType(), 
                ApplicationStatus.DRAFT, 
                LocalDateTime.now(),
                null
        );
        repository.save(app);
        return mapToResponse(app);
    }

    @Override
    public ApplicationResponse update(Long id, CreateApplicationRequest request){
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if(app.getStatus() != ApplicationStatus.DRAFT){
            throw new RuntimeException("Only draft can be updated");
        }
        app.setAmount(request.getAmount());
        app.setLoanType(request.getLoanType());
        app.setTenure(request.getTenure());
        app.setIncome(request.getIncome());
        app.setEmploymentType(request.getEmploymentType());
        repository.save(app);
        return mapToResponse(app);
    }

    @Override
    public ApplicationResponse submit(Long id){
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if(app.getStatus() != ApplicationStatus.DRAFT){
            throw new RuntimeException("Invalid state");
        }
        app.setStatus(ApplicationStatus.SUBMITTED);
        repository.save(app);
        return mapToResponse(app);
    }

    @Override
    public List<ApplicationResponse> getByUser(Long userId){
        return repository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getAll(Long userId, String statusStr) {
        List<LoanApplication> apps;
        if (userId != null && statusStr != null) {
            ApplicationStatus status = ApplicationStatus.valueOf(statusStr.toUpperCase());
            apps = repository.findByUserIdAndStatus(userId, status);
        } else if (userId != null) {
            apps = repository.findByUserId(userId);
        } else if (statusStr != null) {
            ApplicationStatus status = ApplicationStatus.valueOf(statusStr.toUpperCase());
            apps = repository.findByStatus(status);
        } else {
            apps = repository.findAll();
        }
        return apps.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ApplicationResponse getById(Long id, Long userId, String role) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (!"ADMIN".equalsIgnoreCase(role) && !app.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        return mapToResponse(app);
    }

    @Override
    public ApplicationResponse updateStatus(Long id, String statusStr) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        ApplicationStatus newStatus = ApplicationStatus.valueOf(statusStr.toUpperCase());
        app.setStatus(newStatus);
        repository.save(app);
        return mapToResponse(app);
    }

    @Override
    public Map<String, Long> getStats() {
        List<LoanApplication> all = repository.findAll();
        Map<String, Long> stats = new HashMap<>();
        stats.put("TOTAL", (long) all.size());
        stats.put("APPROVED", all.stream().filter(a -> a.getStatus() == ApplicationStatus.APPROVED).count());
        stats.put("REJECTED", all.stream().filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count());
        stats.put("PENDING", all.stream().filter(a -> 
            a.getStatus() != ApplicationStatus.APPROVED && 
            a.getStatus() != ApplicationStatus.REJECTED && 
            a.getStatus() != ApplicationStatus.CLOSED
        ).count());
        return stats;
    }

    @Override
    public List<String> getLoanTypes() {
        return List.of("PERSONAL", "HOME", "CAR");
    }

    @Override
    public Map<String, Object> checkEligibility(Long id) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        boolean eligible = false;
        String reason = "Insufficient income or unfavorable employment status.";

        if (app.getIncome() != null && app.getAmount() != null) {
            if ("UNEMPLOYED".equalsIgnoreCase(app.getEmploymentType())) {
                eligible = false;
                reason = "Unemployed status does not meet eligibility criteria.";
            } else if (app.getIncome() * 10 >= app.getAmount()) {
                eligible = true;
                reason = "Eligible based on income criteria.";
            } else {
                eligible = false;
                reason = "Income is less than the required threshold for the requested amount.";
            }
        } else {
            reason = "Income and Amount details are required for eligibility check.";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("eligible", eligible);
        response.put("reason", reason);
        return response;
    }

    private ApplicationResponse mapToResponse(LoanApplication app) {
        ApplicationResponse res = new ApplicationResponse();
        res.setId(app.getId());
        res.setUserId(app.getUserId());
        res.setAmount(app.getAmount());
        res.setLoanType(app.getLoanType());
        res.setTenure(app.getTenure());
        res.setIncome(app.getIncome());
        res.setEmploymentType(app.getEmploymentType());
        res.setStatus(app.getStatus());
        res.setCreatedAt(app.getCreatedAt());
        return res;
    }
}
