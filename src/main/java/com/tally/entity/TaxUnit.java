package com.tally.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "taxunit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taxunit_id")
    private Long id;

    @JsonProperty("userId")
    @Column(name = "userid", nullable = false)
    private Long userId;

    @JsonProperty("cmpId")
    @Column(name = "cmpid", nullable = false)
    private Long cmpId;

    @JsonProperty("masterId")
    @Column(name = "masterid")
    private Long masterId;

    @JsonProperty("alterId")
    @Column(name = "alterid")
    private Long alterId;

    @Column(name = "guid", unique = true, length = 100)
    private String guid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_active")
    private Boolean isActive = true;

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
