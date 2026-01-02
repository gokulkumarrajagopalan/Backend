package com.tally.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "units")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Units {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private long unitId;

    @JsonProperty("userId")
    @Column(name = "userid")
    private Long userId;

    @JsonProperty("cmpId")
    @Column(name = "cmpid")
    private long cmpId;

    private String guid;

    @JsonProperty("masterId")
    @Column(name = "masterid")
    private long masterId;

    @JsonProperty("alterId")
    @Column(name = "alterid")
    private long alterId;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "is_simple_unit")
    private boolean simpleUnit;

    @Column(name = "reserved_name")
    private String reservedName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
