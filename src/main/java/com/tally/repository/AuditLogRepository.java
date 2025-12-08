package com.tally.repository;

import com.tally.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findByEntityType(String entityType);
    List<AuditLog> findByEntityId(String entityId);
    List<AuditLog> findByCompanyId(Long companyId);
    List<AuditLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    List<AuditLog> findByCompanyIdAndAction(Long companyId, String action);
    List<AuditLog> findByUserIdAndTimestampBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
