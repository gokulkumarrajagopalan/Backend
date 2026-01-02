package com.tally.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_sync_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanySyncStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cmp_id", nullable = false, unique = true)
    private Long cmpId;
    
    @Column(name = "last_alter_id", nullable = false)
    private Long lastAlterId = 0L;
    
    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;
    
    @Column(name = "entity_type", length = 50)
    private String entityType;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}