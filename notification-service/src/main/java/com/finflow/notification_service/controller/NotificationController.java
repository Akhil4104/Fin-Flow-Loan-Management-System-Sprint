package com.finflow.notification_service.controller;

import com.finflow.notification_service.entity.Notification;
import com.finflow.notification_service.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationRepository repo;

    public NotificationController(NotificationRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("#userId.toString() == authentication.principal or hasRole('ADMIN')")
    public Page<Notification> getNotifications(@PathVariable Long userId, Pageable pageable) {
        return repo.findByUserId(userId, pageable);
    }
}