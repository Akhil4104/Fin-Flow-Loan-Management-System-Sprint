package com.finflow.admin_service.service;

import com.finflow.admin_service.dto.DecisionRequest;
import com.finflow.admin_service.dto.DecisionResponse;
import com.finflow.admin_service.entity.AuditLog;
import java.util.List;
import java.util.Map;

public interface AdminService {
    DecisionResponse makeDecision(Long adminId, Long applicationId, DecisionRequest request);
    List<Object> getAllApplications(Long userId, String status);
    Object getApplicationById(Long id);
    Object assignApplication(Long adminId, Long id);
    Object reviewApplication(Long adminId, Long id);
    Object closeApplication(Long adminId, Long id);
    Object verifyDocument(Long adminId, Long docId);
    Object rejectDocument(Long adminId, Long docId);
    List<Object> getAllUsers();
    Object getUserById(Long id);
    Object updateUser(Long adminId, Long id, Object request);
    void deleteUser(Long adminId, Long id);
    Map<String, Long> getDashboardStats();
    List<AuditLog> getAuditLogs();
}
