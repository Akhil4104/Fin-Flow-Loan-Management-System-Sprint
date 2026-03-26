package com.finflow.notification_service.service.impl;

import com.finflow.notification_service.entity.Notification;
import com.finflow.notification_service.repository.NotificationRepository;
import com.finflow.notification_service.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;

    public NotificationServiceImpl(NotificationRepository repo) {
        this.repo = repo;
    }

    @Override
    public void processNotification(Long userId, String message, String type) {

        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type != null ? type : "IN_APP");
        notification.setStatus("SENT");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        repo.save(notification);
    }
}
