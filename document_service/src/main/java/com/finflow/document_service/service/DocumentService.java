package com.finflow.document_service.service;

import com.finflow.document_service.dto.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.List;

public interface DocumentService {
    DocumentResponse upload(Long applicationId, Long userId, MultipartFile file) throws IOException;
    DocumentResponse getById(Long id, Long userId, String role);
    Resource downloadFile(Long id, Long userId, String role) throws IOException;
    DocumentResponse replace(Long id, Long userId, String role, MultipartFile file) throws IOException;
    void delete(Long id, Long userId, String role);
    DocumentResponse verify(Long id);
    DocumentResponse reject(Long id);
    List<DocumentResponse> getByApplication(Long applicationId, Long userId, String role);
}
