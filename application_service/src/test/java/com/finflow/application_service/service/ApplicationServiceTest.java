package com.finflow.application_service.service;

import com.finflow.application_service.dto.ApplicationResponse;
import com.finflow.application_service.dto.CreateApplicationRequest;
import com.finflow.application_service.entity.ApplicationStatus;
import com.finflow.application_service.entity.LoanApplication;
import com.finflow.application_service.repository.LoanApplicationRepository;
import com.finflow.application_service.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private LoanApplicationRepository repository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private LoanApplication testApp;
    private CreateApplicationRequest createRequest;

    @BeforeEach
    void setUp() {
        testApp = new LoanApplication(1L, 1L, 10000.0, "PERSONAL", 12, 2000.0, "SALARIED", ApplicationStatus.DRAFT, null, null);
        
        createRequest = new CreateApplicationRequest();
        createRequest.setAmount(10000.0);
        createRequest.setLoanType("PERSONAL");
        createRequest.setTenure(12);
        createRequest.setIncome(2000.0);
        createRequest.setEmploymentType("SALARIED");
    }

    @Test
    void create_ShouldSaveAndReturnDraft() {
        // Act
        ApplicationResponse result = applicationService.create(1L, createRequest);

        // Assert
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.DRAFT);
        verify(repository).save(any(LoanApplication.class));
    }

    @Test
    void submit_WithDraftStatus_ShouldUpdateToSubmitted() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(testApp));

        // Act
        ApplicationResponse result = applicationService.submit(1L);

        // Assert
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.SUBMITTED);
        verify(repository).save(testApp);
    }

    @Test
    void submit_WithNonDraftStatus_ShouldThrowException() {
        // Arrange
        testApp.setStatus(ApplicationStatus.APPROVED);
        when(repository.findById(1L)).thenReturn(Optional.of(testApp));

        // Act & Assert
        assertThatThrownBy(() -> applicationService.submit(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid state");
    }

    @Test
    void checkEligibility_WhenIncomeIsHighEnough_ShouldBeEligible() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(testApp));

        // Act
        Map<String, Object> result = applicationService.checkEligibility(1L);

        // Assert
        assertThat(result.get("eligible")).isEqualTo(true);
        assertThat(result.get("reason")).isEqualTo("Eligible based on income criteria.");
    }

    @Test
    void checkEligibility_WhenUnemployed_ShouldNotBeEligible() {
        // Arrange
        testApp.setEmploymentType("UNEMPLOYED");
        when(repository.findById(1L)).thenReturn(Optional.of(testApp));

        // Act
        Map<String, Object> result = applicationService.checkEligibility(1L);

        // Assert
        assertThat(result.get("eligible")).isEqualTo(false);
        assertThat(result.get("reason")).isEqualTo("Unemployed status does not meet eligibility criteria.");
    }

    @Test
    void checkEligibility_WhenIncomeIsTooLow_ShouldNotBeEligible() {
        // Arrange
        testApp.setIncome(500.0); // 500 * 10 = 5000 < 10000 (amount)
        when(repository.findById(1L)).thenReturn(Optional.of(testApp));

        // Act
        Map<String, Object> result = applicationService.checkEligibility(1L);

        // Assert
        assertThat(result.get("eligible")).isEqualTo(false);
        assertThat(result.get("reason")).isEqualTo("Income is less than the required threshold for the requested amount.");
    }
}
