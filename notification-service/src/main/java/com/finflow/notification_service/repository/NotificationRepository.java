package com.finflow.notification_service.repository;

import com.finflow.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);
}
