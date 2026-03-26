package com.finflow.notification_service.service;

public interface NotificationService {

    void processNotification(Long userId, String message, String type);
}