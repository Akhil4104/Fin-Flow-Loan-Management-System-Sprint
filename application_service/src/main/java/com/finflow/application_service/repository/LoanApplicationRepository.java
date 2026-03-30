package com.finflow.application_service.repository;

import com.finflow.application_service.entity.LoanApplication;
import com.finflow.application_service.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication,Long> {
    List<LoanApplication> findByUserId(Long userId);
    List<LoanApplication> findByStatus(ApplicationStatus status);
    List<LoanApplication> findByUserIdAndStatus(Long userId, ApplicationStatus status);
}
