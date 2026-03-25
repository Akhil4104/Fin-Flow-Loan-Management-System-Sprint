package com.finflow.document_service.service;

import com.finflow.document_service.dto.DocumentResponse;
import com.finflow.document_service.entity.Document;
import com.finflow.document_service.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository repository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private Document testDoc;

    @BeforeEach
    void setUp() {
        testDoc = new Document(1L, "test.pdf", "application/pdf", "/tmp/test.pdf", "PENDING");
        testDoc.setId(10L);
    }

    @Test
    void verify_ShouldUpdateStatusToVerified() {
        // Arrange
        when(repository.findById(10L)).thenReturn(Optional.of(testDoc));

        // Act
        DocumentResponse result = documentService.verify(10L);

        // Assert
        assertThat(result.getStatus()).isEqualTo("VERIFIED");
        verify(repository).save(testDoc);
    }

    @Test
    void reject_ShouldUpdateStatusToRejected() {
        // Arrange
        when(repository.findById(10L)).thenReturn(Optional.of(testDoc));

        // Act
        DocumentResponse result = documentService.reject(10L);

        // Assert
        assertThat(result.getStatus()).isEqualTo("REJECTED");
        verify(repository).save(testDoc);
    }

    @Test
    void getById_ShouldReturnDocumentResponse() {
        // Arrange
        when(repository.findById(10L)).thenReturn(Optional.of(testDoc));

        // Act
        DocumentResponse result = documentService.getById(10L);

        // Assert
        assertThat(result.getFileName()).isEqualTo("test.pdf");
        assertThat(result.getStatus()).isEqualTo("PENDING");
    }
}
