package com.finflow.admin_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "document-service")
public interface DocumentClient {

    @PutMapping("/documents/{id}/verify")
    Object verifyDocument(@PathVariable("id") Long id);

    @PutMapping("/documents/{id}/reject")
    Object rejectDocument(@PathVariable("id") Long id);
}
