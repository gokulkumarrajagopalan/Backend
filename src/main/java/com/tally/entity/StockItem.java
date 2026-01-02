package com.tally.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stockitem_id")
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

    @Column(name = "parent")
    private String parent;

    @Column(name = "category")
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "mailing_name")
    private String mailingName;

    @Column(name = "reserved_name")
    private String reservedName;

    // Units
    @Column(name = "base_units", length = 100)
    private String baseUnits;

    @Column(name = "additional_units", length = 100)
    private String additionalUnits;

    // Inventory
    @Column(name = "opening_balance", precision = 19, scale = 4)
    private BigDecimal openingBalance;

    @Column(name = "opening_value", precision = 19, scale = 4)
    private BigDecimal openingValue;

    @Column(name = "opening_rate", precision = 19, scale = 4)
    private BigDecimal openingRate;

    // Costing
    @Column(name = "costing_method", length = 50)
    private String costingMethod;

    @Column(name = "valuation_method", length = 50)
    private String valuationMethod;

    // GST
    @Column(name = "gst_type_of_supply", length = 50)
    private String gstTypeOfSupply;

    @Column(name = "hsn_code", length = 50)
    private String hsnCode;

    // Flags
    @Column(name = "is_batch_wise_on")
    private Boolean batchWiseOn;

    @Column(name = "is_cost_centers_on")
    private Boolean costCentersOn;

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
