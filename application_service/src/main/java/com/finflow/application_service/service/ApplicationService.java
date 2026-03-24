package com.finflow.application_service.service;

import com.finflow.application_service.dto.ApplicationResponse;
import com.finflow.application_service.dto.CreateApplicationRequest;
import com.finflow.application_service.entity.ApplicationStatus;
import com.finflow.application_service.entity.LoanApplication;
import com.finflow.application_service.repository.LoanApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
    private final LoanApplicationRepository repository;

    public ApplicationService(LoanApplicationRepository repository){
        this.repository=repository;
    }

    public ApplicationResponse create(Long userId, CreateApplicationRequest request){
        LoanApplication app=new LoanApplication(userId,request.getAmount());
        repository.save(app);
        return mapToResponse(app);
    }
    public ApplicationResponse update(Long id,CreateApplicationRequest request){
        LoanApplication app=repository.findById(id)
                .orElseThrow(()->new RuntimeException("Application not found"));

        if(app.getStatus()!= ApplicationStatus.DRAFT){
            throw new RuntimeException("Only draft can be updated");
        }
        app.setAmount(request.getAmount());
        repository.save(app);
        return mapToResponse(app);
    }
    public ApplicationResponse submit(Long id){
        LoanApplication app=repository.findById(id)
                .orElseThrow(()->new RuntimeException("Application not found"));

        if(app.getStatus()!=ApplicationStatus.DRAFT){
            throw new RuntimeException("Invalid state");
        }
        app.setStatus(ApplicationStatus.SUBMITTED);
        repository.save(app);
        return mapToResponse(app);
    }
    public List<ApplicationResponse>getByUser(Long userId){
        return repository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    private ApplicationResponse mapToResponse(LoanApplication app) {
        ApplicationResponse res = new ApplicationResponse();
        res.setId(app.getId());
        res.setAmount(app.getAmount());
        res.setStatus(app.getStatus());
        res.setCreatedAt(app.getCreatedAt());
        return res;
    }

}
