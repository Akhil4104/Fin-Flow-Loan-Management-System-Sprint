package com.finflow.admin_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "application-service")
public interface ApplicationClient {

    @GetMapping("/applications")
    List<Object> getAllApplications(@RequestParam(required = false) Long userId, @RequestParam(required = false) String status);

    @GetMapping("/applications/{id}")
    Object getApplicationById(@PathVariable("id") Long id);

    @PutMapping("/applications/{id}/status")
    Object updateApplicationStatus(@PathVariable("id") Long id, @RequestParam("status") String status);

    @GetMapping("/applications/stats")
    Map<String, Long> getApplicationStats();
}
