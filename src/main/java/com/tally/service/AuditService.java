package com.tally.service;

import com.tally.entity.AuditLog;
import com.tally.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    public AuditLog logAction(Long userId, String username, String action, String entityType, 
                             String entityId, String oldValue, String newValue, String changeDescription, 
                             String ipAddress, Long companyId) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setChangeDescription(changeDescription);
        log.setIpAddress(ipAddress);
        log.setCompanyId(companyId);
        log.setTimestamp(LocalDateTime.now());
        log.setStatus("SUCCESS");
        return auditLogRepository.save(log);
    }

    public AuditLog logFailedAction(Long userId, String username, String action, String entityType,
                                   String entityId, String reason, Long companyId) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setCompanyId(companyId);
        log.setStatus("FAILED");
        log.setRemarks(reason);
        log.setTimestamp(LocalDateTime.now());
        return auditLogRepository.save(log);
    }

    public List<AuditLog> getUserAuditLogs(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    public List<AuditLog> getCompanyAuditLogs(Long companyId) {
        return auditLogRepository.findByCompanyId(companyId);
    }

    public List<AuditLog> getEntityAuditLogs(String entityId) {
        return auditLogRepository.findByEntityId(entityId);
    }

    public List<AuditLog> getAuditLogsByAction(Long companyId, String action) {
        return auditLogRepository.findByCompanyIdAndAction(companyId, action);
    }

    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return auditLogRepository.findByTimestampBetween(startTime, endTime);
    }
}
