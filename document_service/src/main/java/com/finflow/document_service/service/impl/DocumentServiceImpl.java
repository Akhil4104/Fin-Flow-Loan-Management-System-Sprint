package com.finflow.document_service.service.impl;

import com.finflow.document_service.client.ApplicationClient;
import com.finflow.document_service.client.dto.ApplicationOwnerResponse;
import com.finflow.document_service.dto.DocumentResponse;
import com.finflow.document_service.entity.Document;
import com.finflow.document_service.repository.DocumentRepository;
import com.finflow.document_service.service.DocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository repository;
    private final ApplicationClient applicationClient;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public DocumentServiceImpl(DocumentRepository repository, ApplicationClient applicationClient){
        this.repository=repository;
        this.applicationClient = applicationClient;
    }

    @Override
    public DocumentResponse upload(Long applicationId, Long userId, MultipartFile file) throws IOException {
        ApplicationOwnerResponse application = requireApplicationAccess(applicationId, userId, "APPLICANT");
        File dir = new File(uploadDir).getAbsoluteFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = uploadDir + file.getOriginalFilename();
        File dest = new File(filePath).getAbsoluteFile();
        file.transferTo(dest);
        Document document = new Document(application.getId(), application.getUserId(), file.getOriginalFilename(), file.getContentType(), filePath, "PENDING");
        repository.save(document);
        return map(document);
    }

    @Override
    public DocumentResponse getById(Long id, Long userId, String role) {
        Document doc = repository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        validateAccess(doc, userId, role);
        return map(doc);
    }

    @Override
    public Resource downloadFile(Long id, Long userId, String role) throws IOException {
        Document doc = repository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        validateAccess(doc, userId, role);
        Path path = Paths.get(doc.getFilePath());
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }

    @Override
    public DocumentResponse replace(Long id, Long userId, String role, MultipartFile file) throws IOException {
        Document doc = repository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        validateAccess(doc, userId, role);
        
        // Delete old file
        File oldFile = new File(doc.getFilePath());
        if (oldFile.exists()) {
            oldFile.delete();
        }

        // Save new file
        File dir = new File(uploadDir).getAbsoluteFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = uploadDir + file.getOriginalFilename();
        File dest = new File(filePath).getAbsoluteFile();
        file.transferTo(dest);
        
        doc.setFileName(file.getOriginalFilename());
        doc.setFileType(file.getContentType());
        doc.setFilePath(filePath);
        doc.setStatus("PENDING");
        doc.setUploadedAt(LocalDateTime.now());
        
        repository.save(doc);
        return map(doc);
    }

    @Override
    public void delete(Long id, Long userId, String role) {
        Document doc = repository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        validateAccess(doc, userId, role);
        File file = new File(doc.getFilePath());
        if (file.exists()) {
            file.delete();
        }
        repository.delete(doc);
    }

    @Override
    public DocumentResponse verify(Long id) {
        Document doc = repository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        doc.setStatus("VERIFIED");
        repository.save(doc);
        return map(doc);
    }

    @Override
    public DocumentResponse reject(Long id) {
        Document doc = repository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        doc.setStatus("REJECTED");
        repository.save(doc);
        return map(doc);
    }

    @Override
    public List<DocumentResponse> getByApplication(Long applicationId, Long userId, String role) {
        ApplicationOwnerResponse application = requireApplicationAccess(applicationId, userId, role);
        backfillLegacyOwners(applicationId, application.getUserId());
        List<Document> documents = isAdmin(role)
                ? repository.findByApplicationId(applicationId)
                : repository.findByApplicationIdAndOwnerUserId(applicationId, application.getUserId());
        return documents
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private void validateAccess(Document doc, Long userId, String role) {
        if (isAdmin(role)) {
            return;
        }
        Long ownerUserId = resolveOwnerUserId(doc, userId, role);
        if (!ownerUserId.equals(userId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private Long resolveOwnerUserId(Document doc, Long userId, String role) {
        if (doc.getOwnerUserId() != null) {
            return doc.getOwnerUserId();
        }
        ApplicationOwnerResponse application = requireApplicationAccess(doc.getApplicationId(), userId, role);
        doc.setOwnerUserId(application.getUserId());
        repository.save(doc);
        return application.getUserId();
    }

    private void backfillLegacyOwners(Long applicationId, Long ownerUserId) {
        List<Document> documents = repository.findByApplicationId(applicationId);
        boolean updated = false;
        for (Document document : documents) {
            if (document.getOwnerUserId() == null) {
                document.setOwnerUserId(ownerUserId);
                updated = true;
            }
        }
        if (updated) {
            repository.saveAll(documents);
        }
    }

    private ApplicationOwnerResponse requireApplicationAccess(Long applicationId, Long userId, String role) {
        ApplicationOwnerResponse application = applicationClient.getApplication(applicationId, userId, role);
        if (application == null || application.getUserId() == null) {
            throw new RuntimeException("Application not found");
        }
        return application;
    }

    private boolean isAdmin(String role) {
        return "ADMIN".equalsIgnoreCase(role);
    }

    private DocumentResponse map(Document doc) {
        DocumentResponse res = new DocumentResponse();
        res.setId(doc.getId());
        res.setFileName(doc.getFileName());
        res.setFileType(doc.getFileType());
        res.setUploadedAt(doc.getUploadedAt());
        res.setStatus(doc.getStatus());
        return res;
    }
}
