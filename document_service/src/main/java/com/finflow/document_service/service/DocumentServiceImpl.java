package com.finflow.document_service.service;

import com.finflow.document_service.dto.DocumentResponse;
import com.finflow.document_service.entity.Document;
import com.finflow.document_service.repository.DocumentRepository;
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

    @Value("${file.upload-dir}")
    private String uploadDir;

    public DocumentServiceImpl(DocumentRepository repository){
        this.repository=repository;
    }

    @Override
    public DocumentResponse upload(Long applicationId, MultipartFile file) throws IOException {
        File dir = new File(uploadDir).getAbsoluteFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = uploadDir + file.getOriginalFilename();
        File dest = new File(filePath).getAbsoluteFile();
        file.transferTo(dest);
        Document document = new Document(applicationId, file.getOriginalFilename(), file.getContentType(), filePath, "PENDING");
        repository.save(document);
        return map(document);
    }

    @Override
    public DocumentResponse getById(Long id) {
        Document doc = repository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        return map(doc);
    }

    @Override
    public Resource downloadFile(Long id) throws IOException {
        Document doc = repository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        Path path = Paths.get(doc.getFilePath());
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }

    @Override
    public DocumentResponse replace(Long id, MultipartFile file) throws IOException {
        Document doc = repository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        
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
    public void delete(Long id) {
        Document doc = repository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
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
    public List<DocumentResponse> getByApplication(Long applicationId) {
        return repository.findByApplicationId(applicationId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
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
