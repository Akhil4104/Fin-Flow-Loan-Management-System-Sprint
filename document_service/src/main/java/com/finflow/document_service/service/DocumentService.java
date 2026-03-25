package com.finflow.document_service.service;

import com.finflow.document_service.dto.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.List;

public interface DocumentService {
    DocumentResponse upload(Long applicationId, MultipartFile file) throws IOException;
    DocumentResponse getById(Long id);
    Resource downloadFile(Long id) throws IOException;
    DocumentResponse replace(Long id, MultipartFile file) throws IOException;
    void delete(Long id);
    DocumentResponse verify(Long id);
    DocumentResponse reject(Long id);
    List<DocumentResponse> getByApplication(Long applicationId);
}
