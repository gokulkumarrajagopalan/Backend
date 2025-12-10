package com.tally.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledgers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cmpid", "led_name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ledger {
    
    // ========== PRIMARY KEY & REFERENCES ==========
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledid")
    private Long ledId;
    
    @Column(name = "userid", nullable = false)
    private Long userId;
    
    @Column(name = "cmpid", nullable = false)
    private Long cmpId;
    
    // ========== TALLY IDENTIFIERS ==========
    @Column(name = "masterid", nullable = false)
    private Long masterId;
    
    @Column(name = "alterid")
    private Long alterId;
    
    @Column(name = "guid", unique = true, nullable = false, length = 100)
    private String guid;
    
    // ========== LEDGER BASIC INFORMATION ==========
    @Column(name = "led_name", nullable = false)
    private String ledName;
    
    @Column(name = "led_code", length = 50)
    private String ledCode;
    
    @Column(name = "led_alias")
    private String ledAlias;
    
    @Column(name = "led_parent")
    private String ledParent;
    
    @Column(name = "led_primary_group")
    private String ledPrimaryGroup;
    
    @Column(name = "led_description", columnDefinition = "TEXT")
    private String ledDescription;
    
    @Column(name = "led_note", columnDefinition = "TEXT")
    private String ledNote;
    
    // ========== LEDGER CLASSIFICATION ==========
    @Column(name = "is_revenue")
    private Boolean isRevenue = false;
    
    @Column(name = "is_reserved")
    private Boolean isReserved = false;
    
    @Column(name = "reserved_name")
    private String reservedName;
    
    @Column(name = "last_parent")
    private String lastParent;
    
    // ========== LEDGER FEATURES ==========
    @Column(name = "led_billwise_on")
    private Boolean ledBillwiseOn = false;
    
    @Column(name = "led_is_costcentre_on")
    private Boolean ledIsCostcentreOn = false;
    
    // ========== LEDGER HIERARCHY ==========
    @Column(name = "parent_ledid")
    private Long parentLedId;
    
    @Column(name = "grpid")
    private Long grpId;
    
    @Column(name = "level_number")
    private Integer levelNumber = 0;
    
    @Column(name = "full_path", length = 1000)
    private String fullPath;
    
    @Column(name = "parent_hierarchy", columnDefinition = "TEXT")
    private String parentHierarchy;
    
    // ========== CONTACT INFORMATION ==========
    @Column(name = "led_mailing_name")
    private String ledMailingName;
    
    @Column(name = "led_address_1")
    private String ledAddress1;
    
    @Column(name = "led_address_2")
    private String ledAddress2;
    
    @Column(name = "led_address_3")
    private String ledAddress3;
    
    @Column(name = "led_address_4")
    private String ledAddress4;
    
    @Column(name = "led_state", length = 100)
    private String ledState;
    
    @Column(name = "led_country", length = 100)
    private String ledCountry;
    
    @Column(name = "led_pincode", length = 20)
    private String ledPincode;
    
    @Column(name = "led_contact", length = 100)
    private String ledContact;
    
    @Column(name = "led_phone", length = 20)
    private String ledPhone;
    
    @Column(name = "led_country_isd_code", length = 10)
    private String ledCountryIsdCode;
    
    @Column(name = "led_mobile", length = 20)
    private String ledMobile;
    
    @Column(name = "led_email", length = 100)
    private String ledEmail;
    
    @Column(name = "led_website")
    private String ledWebsite;
    
    // ========== FINANCIAL INFORMATION ==========
    @Column(name = "led_opening_balance", precision = 18, scale = 2)
    private BigDecimal ledOpeningBalance = BigDecimal.ZERO;
    
    @Column(name = "currency_name", length = 50)
    private String currencyName = "₹";
    
    @Column(name = "income_tax_number", length = 50)
    private String incomeTaxNumber;
    
    // ========== GST CONFIGURATION (INDIA) ==========
    @Column(name = "gst_applicable")
    private Boolean gstApplicable = false;
    
    @Column(name = "gst_registration_type", length = 50)
    private String gstRegistrationType;
    
    @Column(name = "gst_registration_date")
    private LocalDate gstRegistrationDate;
    
    @Column(name = "gst_gstin", length = 15)
    private String gstGstin;
    
    @Column(name = "gst_is_freezone")
    private Boolean gstIsFreezone = false;
    
    @Column(name = "gst_state", length = 100)
    private String gstState;
    
    @Column(name = "gst_place_of_supply", length = 100)
    private String gstPlaceOfSupply;
    
    @Column(name = "gst_transporter_id", length = 50)
    private String gstTransporterId;
    
    @Column(name = "gst_is_other_territory_assessee")
    private Boolean gstIsOtherTerritoryAssessee = false;
    
    @Column(name = "gst_consider_purchase_for_export")
    private Boolean gstConsiderPurchaseForExport = false;
    
    @Column(name = "gst_is_transporter")
    private Boolean gstIsTransporter = false;
    
    @Column(name = "gst_is_common_party")
    private Boolean gstIsCommonParty = false;
    
    // ========== VAT CONFIGURATION (GCC) ==========
    @Column(name = "vat_applicable")
    private Boolean vatApplicable = false;
    
    @Column(name = "vat_registration_type", length = 50)
    private String vatRegistrationType;
    
    @Column(name = "vat_registration_date")
    private LocalDate vatRegistrationDate;
    
    @Column(name = "vat_tin_number", length = 50)
    private String vatTinNumber;
    
    @Column(name = "vat_is_freezone")
    private Boolean vatIsFreezone = false;
    
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
    
    @Column(name = "mailing_details_applicable_from")
    private LocalDate mailingDetailsApplicableFrom;
    
    @Column(name = "gst_details_applicable_from")
    private LocalDate gstDetailsApplicableFrom;
    
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
