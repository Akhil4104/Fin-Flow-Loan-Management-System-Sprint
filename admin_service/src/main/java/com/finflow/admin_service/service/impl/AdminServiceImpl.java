package com.finflow.admin_service.service.impl;

import com.finflow.admin_service.dto.DecisionRequest;
import com.finflow.admin_service.dto.DecisionResponse;
import com.finflow.admin_service.entity.Decision;
import com.finflow.admin_service.repository.DecisionRepository;
import com.finflow.admin_service.entity.AuditLog;
import com.finflow.admin_service.repository.AuditLogRepository;
import com.finflow.admin_service.service.AdminService;
import com.finflow.admin_service.client.ApplicationClient;
import com.finflow.admin_service.client.DocumentClient;
import com.finflow.admin_service.client.AuthClient;
import com.finflow.admin_service.producer.NotificationProducer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final DecisionRepository decisionRepository;
    private final AuditLogRepository auditLogRepository;
    private final ApplicationClient applicationClient;
    private final DocumentClient documentClient;
    private final AuthClient authClient;
    private final NotificationProducer notificationProducer;

    public AdminServiceImpl(DecisionRepository decisionRepository,
                            AuditLogRepository auditLogRepository,
                            ApplicationClient applicationClient,
                            DocumentClient documentClient,
                            AuthClient authClient,
                            NotificationProducer notificationProducer) {
        this.decisionRepository = decisionRepository;
        this.auditLogRepository = auditLogRepository;
        this.applicationClient = applicationClient;
        this.documentClient = documentClient;
        this.authClient = authClient;
        this.notificationProducer = notificationProducer;
    }

    private void logAction(Long adminId, String action, Long targetId, String details) {
        auditLogRepository.save(new AuditLog(adminId, action, targetId, details));
    }

    private void notifyApplicant(Long applicationId, String message) {
        try {
            Map<String, Object> appMap = (Map<String, Object>) applicationClient.getApplicationById(applicationId);
            if (appMap != null && appMap.get("userId") != null) {
                Long userId = Long.valueOf(appMap.get("userId").toString());
                notificationProducer.sendNotification(userId, message, "IN_APP");
            }
        } catch (Exception e) {
            System.err.println("Failed to notify user for application " + applicationId + ": " + e.getMessage());
        }
    }

    @Override
    public DecisionResponse makeDecision(Long adminId, Long applicationId, DecisionRequest request) {
        String normalizedDecision = request.getDecision() == null ? null : request.getDecision().trim().toUpperCase();
        if (!"APPROVED".equals(normalizedDecision) && !"REJECTED".equals(normalizedDecision)) {
            throw new RuntimeException("Decision must be APPROVED or REJECTED");
        }

        Optional<Decision> existing = decisionRepository.findByApplicationId(applicationId);
        if (existing.isPresent() && existing.get().getDecision().equalsIgnoreCase(normalizedDecision)) {
            throw new RuntimeException("Decision is already " + normalizedDecision);
        }

        Decision decision = new Decision(
                applicationId,
                normalizedDecision,
                request.getRemarks()
        );
        decisionRepository.save(decision);
        applicationClient.updateApplicationStatus(applicationId, normalizedDecision);
        
        logAction(adminId, "DECIDE_APPLICATION", applicationId, "Decision: " + normalizedDecision);
        notifyApplicant(applicationId, "Your application has been " + normalizedDecision);
        DecisionResponse res = new DecisionResponse();
        res.setApplicationId(applicationId);
        res.setDecision(normalizedDecision);
        res.setRemarks(request.getRemarks());
        res.setDecidedAt(decision.getDecidedAt());

        return res;
    }

    @Override
    public List<Object> getAllApplications(Long userId, String status) {
        return applicationClient.getAllApplications(userId, status);
    }

    @Override
    public Object getApplicationById(Long id) {
        return applicationClient.getApplicationById(id);
    }

    @Override
    public Object assignApplication(Long adminId, Long id) {
        logAction(adminId, "ASSIGN_APPLICATION", id, "Assigned to review");
        notifyApplicant(id, "Your application is now under review");
        return applicationClient.updateApplicationStatus(id, "UNDER_REVIEW");
    }

    @Override
    public Object reviewApplication(Long adminId, Long id) {
        logAction(adminId, "REVIEW_APPLICATION", id, "Reviewing documents");
        notifyApplicant(id, "Your application requires document verification");
        return applicationClient.updateApplicationStatus(id, "DOCS_PENDING");
    }

    @Override
    public Object closeApplication(Long adminId, Long id) {
        logAction(adminId, "CLOSE_APPLICATION", id, "Closed application workflow");
        notifyApplicant(id, "Your application has been closed");
        return applicationClient.updateApplicationStatus(id, "CLOSED");
    }

    @Override
    public Object verifyDocument(Long adminId, Long docId) {
        logAction(adminId, "VERIFY_DOCUMENT", docId, "Document Verified");
        return documentClient.verifyDocument(docId);
    }

    @Override
    public Object rejectDocument(Long adminId, Long docId) {
        logAction(adminId, "REJECT_DOCUMENT", docId, "Document Rejected");
        return documentClient.rejectDocument(docId);
    }

    @Override
    public List<Object> getAllUsers() {
        return authClient.getAllUsers();
    }

    @Override
    public Object getUserById(Long id) {
        return authClient.getUserById(id);
    }

    @Override
    public Object updateUser(Long adminId, Long id, Object request) {
        logAction(adminId, "UPDATE_USER", id, "User details updated");
        return authClient.updateUser(id, request);
    }

    @Override
    public void deleteUser(Long adminId, Long id) {
        logAction(adminId, "DELETE_USER", id, "User deleted");
        authClient.deleteUser(id);
    }

    @Override
    public Map<String, Long> getDashboardStats() {
        return applicationClient.getApplicationStats();
    }

    @Override
    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAll();
    }
}
