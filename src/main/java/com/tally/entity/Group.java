package com.tally.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "groups", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "cmpid", "grp_name" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    // ========== PRIMARY KEY & REFERENCES ==========
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grpid")
    private Long grpId;

    @JsonProperty("userId")
    @Column(name = "userid", nullable = false)
    private Long userId;

    @JsonProperty("cmpId")
    @Column(name = "cmpid", nullable = false)
    private Long cmpId;

    // ========== TALLY IDENTIFIERS ==========
    @JsonProperty("masterId")
    @Column(name = "masterid", nullable = false)
    private Long masterId;

    @JsonProperty("alterId")
    @Column(name = "alterid")
    private Long alterId;

    @Column(name = "guid", unique = true, nullable = false, length = 100)
    private String guid;

    // ========== GROUP INFORMATION ==========
    @Column(name = "grp_name", nullable = false)
    private String grpName;

    @Column(name = "grp_code", length = 50)
    private String grpCode;

    @Column(name = "grp_alias")
    private String grpAlias;

    @Column(name = "grp_parent")
    private String grpParent;

    @Column(name = "grp_primary_group")
    private String grpPrimaryGroup;

    @Column(name = "grp_nature", length = 50)
    private String grpNature;

    // ========== GROUP CLASSIFICATION ==========
    @Column(name = "is_revenue")
    private Boolean isRevenue = false;

    @Column(name = "is_reserved")
    private Boolean isReserved = false;

    @Column(name = "reserved_name")
    private String reservedName;

    // ========== GROUP HIERARCHY ==========
    @Column(name = "parent_grpid")
    private Long parentGrpId;

    @Column(name = "level_number")
    private Integer levelNumber = 0;

    @Column(name = "full_path", length = 1000)
    private String fullPath;

    // ========== ADDITIONAL NAMES (Multilingual Support) ==========
    @Column(name = "language_id")
    private Integer languageId = 1033;

    @Column(name = "alternate_names", columnDefinition = "TEXT")
    private String alternateNames;

    // ========== STATUS & METADATA ==========
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "sync_status", length = 50)
    private String syncStatus = "SYNCED";

    @Column(name = "last_sync_date")
    private LocalDateTime lastSyncDate;

    // ========== AUDIT FIELDS ==========
    @Column(name = "created_at", nullable = false, updatable = false)
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
