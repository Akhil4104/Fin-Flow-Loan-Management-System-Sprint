package com.finflow.admin_service.repository;

import com.finflow.admin_service.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByAdminIdOrderByTimestampDesc(Long adminId);
}
