package com.tally.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "godowns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Godown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "godown_id")
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

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "reserved_name")
    private String reservedName;

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
