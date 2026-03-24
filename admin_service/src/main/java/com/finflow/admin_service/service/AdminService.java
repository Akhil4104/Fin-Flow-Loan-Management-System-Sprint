package com.finflow.admin_service.service;

import com.finflow.admin_service.dto.DecisionRequest;
import com.finflow.admin_service.dto.DecisionResponse;
import com.finflow.admin_service.entity.Decision;
import com.finflow.admin_service.repository.DecisionRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final DecisionRepository repository;

    public AdminService(DecisionRepository repository){
        this.repository=repository;
    }

    public DecisionResponse makeDecision(Long applicationId,DecisionRequest request){
        Decision decision=new Decision(
                applicationId,
                request.getDecision(),
                request.getRemarks()
        );
        repository.save(decision);

        DecisionResponse res = new DecisionResponse();
        res.setApplicationId(applicationId);
        res.setDecision(request.getDecision());
        res.setRemarks(request.getRemarks());
        res.setDecidedAt(decision.getDecidedAt());

        return res;
    }
}
