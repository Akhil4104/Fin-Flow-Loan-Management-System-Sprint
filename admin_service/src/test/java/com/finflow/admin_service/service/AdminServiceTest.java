package com.finflow.admin_service.service;

import com.finflow.admin_service.dto.DecisionRequest;
import com.finflow.admin_service.dto.DecisionResponse;
import com.finflow.admin_service.entity.AuditLog;
import com.finflow.admin_service.entity.Decision;
import com.finflow.admin_service.repository.AuditLogRepository;
import com.finflow.admin_service.repository.DecisionRepository;
import com.finflow.admin_service.service.impl.AdminServiceImpl;
import com.finflow.admin_service.client.ApplicationClient;
import com.finflow.admin_service.client.DocumentClient;
import com.finflow.admin_service.client.AuthClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private DecisionRepository decisionRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private ApplicationClient applicationClient;

    @Mock
    private DocumentClient documentClient;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private AdminServiceImpl adminService;

    private DecisionRequest decisionRequest;

    @BeforeEach
    void setUp() {
        decisionRequest = new DecisionRequest();
        decisionRequest.setDecision("APPROVED");
        decisionRequest.setRemarks("Good credit score");
    }

    @Test
    void makeDecision_WhenNewDecision_ShouldSaveAndLog() {
        // Arrange
        when(decisionRepository.findByApplicationId(1L)).thenReturn(Optional.empty());

        // Act
        DecisionResponse result = adminService.makeDecision(10L, 1L, decisionRequest);

        // Assert
        assertThat(result.getDecision()).isEqualTo("APPROVED");
        verify(decisionRepository).save(any(Decision.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void makeDecision_WhenDuplicateDecision_ShouldThrowException() {
        // Arrange
        Decision existing = new Decision(1L, "APPROVED", "Old remarks");
        when(decisionRepository.findByApplicationId(1L)).thenReturn(Optional.of(existing));

        // Act & Assert
        assertThatThrownBy(() -> adminService.makeDecision(10L, 1L, decisionRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Decision is already APPROVED");
    }

    @Test
    void assignApplication_ShouldUpdateStatusAndLog() {
        // Arrange
        when(applicationClient.updateApplicationStatus(1L, "UNDER_REVIEW")).thenReturn(new Object());

        // Act
        Object result = adminService.assignApplication(10L, 1L);

        // Assert
        assertThat(result).isNotNull();
        verify(auditLogRepository).save(any(AuditLog.class));
        verify(applicationClient).updateApplicationStatus(1L, "UNDER_REVIEW");
    }

    @Test
    void verifyDocument_ShouldCallClientAndLog() {
        // Arrange
        when(documentClient.verifyDocument(1L)).thenReturn(new Object());

        // Act
        Object result = adminService.verifyDocument(10L, 1L);

        // Assert
        assertThat(result).isNotNull();
        verify(auditLogRepository).save(any(AuditLog.class));
        verify(documentClient).verifyDocument(1L);
    }
}
