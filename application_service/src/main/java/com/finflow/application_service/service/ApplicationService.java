package com.finflow.application_service.service;

import com.finflow.application_service.dto.ApplicationResponse;
import com.finflow.application_service.dto.CreateApplicationRequest;
import java.util.List;
import java.util.Map;

public interface ApplicationService {
    ApplicationResponse create(Long userId, CreateApplicationRequest request);
    ApplicationResponse update(Long id, CreateApplicationRequest request);
    ApplicationResponse submit(Long id);
    List<ApplicationResponse> getByUser(Long userId);
    List<ApplicationResponse> getAll(Long userId, String statusStr);
    ApplicationResponse getById(Long id, Long userId, String role);
    ApplicationResponse updateStatus(Long id, String statusStr);
    Map<String, Long> getStats();
    List<String> getLoanTypes();
    Map<String, Object> checkEligibility(Long id);
}
