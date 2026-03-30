package com.finflow.document_service.controller;

import com.finflow.document_service.dto.DocumentResponse;
import com.finflow.document_service.entity.Document;
import com.finflow.document_service.repository.DocumentRepository;
import com.finflow.document_service.service.DocumentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/documents")
@Tag(name = "Document Management", description = "Endpoints for uploading and managing documents")
public class DocumentController {
    private final DocumentService service;

    public DocumentController(DocumentService service){
        this.service=service;
    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('APPLICANT')")
    @Operation(summary = "Upload Document", description = "Upload a document related to an application. Applicants only.")
    public DocumentResponse upload(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long applicationId,
            @RequestPart("file") MultipartFile file)throws IOException{
        return service.upload(applicationId, userId, file);
    }
    @GetMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @Operation(summary = "Get Documents by Application", description = "Retrieve all documents for a given application ID. Applicants and Admins only.")
    public List<DocumentResponse>getDocs(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long applicationId){
        return service.getByApplication(applicationId, userId, role);
    }

    @GetMapping("/file/{id}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @Operation(summary = "Get Document Metadata", description = "Fetch metadata details for a specific document by its ID.")
    public DocumentResponse getDocById(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id) {
        return service.getById(id, userId, role);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @Operation(summary = "Download Document", description = "Download the actual file content for a given document.")
    public ResponseEntity<Resource> downloadFile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id) throws IOException {
        Resource resource = service.downloadFile(id, userId, role);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @Operation(summary = "Replace Document", description = "Replace an existing document file with a new one.")
    public DocumentResponse replaceDoc(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) throws IOException {
        return service.replace(id, userId, role, file);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @Operation(summary = "Delete Document", description = "Delete a document from the system.")
    public ResponseEntity<Void> deleteDoc(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id) {
        service.delete(id, userId, role);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify Document", description = "Mark a document as verified. Admins only.")
    public DocumentResponse verifyDocument(@PathVariable Long id){
        return service.verify(id);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject Document", description = "Mark a document as rejected. Admins only.")
    public DocumentResponse rejectDocument(@PathVariable Long id){
        return service.reject(id);
    }
}
