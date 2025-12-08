package com.tally.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
public class Company {
    // ========== IDENTITY & REFERENCE ==========
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonProperty("userId")
    @Column(name = "userid", nullable = false)
    private Long userId;
    
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(36)")
    private String companyGuid;
    
    // ========== BASIC COMPANY INFORMATION ==========
    @Column(nullable = false)
    private String name;
    
    @Column(name = "mailing_name")
    private String mailingName;
    
    @Column(name = "pan_number")
    private String panNumber;
    
    @Column(name = "business_type")
    private String businessType;
    
    // ========== COMPANY STATUS & METADATA ==========
    @Column(name = "status")
    private String status = "ACTIVE";
    
    @Column(name = "imported_from")
    private String importedFrom;
    
    @Column(name = "imported_date")
    private LocalDate importedDate;
    
    @Column(name = "sync_status")
    private String syncStatus;
    
    @Column(name = "last_sync_date")
    private LocalDateTime lastSyncDate;
    
    // ========== ADDRESS DETAILS ==========
    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;
    
    @Column(name = "address_line_2")
    private String addressLine2;
    
    @Column(name = "address_line_3")
    private String addressLine3;
    
    @Column(name = "address_line_4")
    private String addressLine4;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "country", nullable = false)
    private String country = "India";
    
    @Column(name = "pincode")
    private String pincode;
    
    // ========== CONTACT INFORMATION ==========
    @Column(name = "telephone")
    private String telephone;
    
    @Column(name = "mobile")
    private String mobile;
    
    @Column(name = "fax")
    private String fax;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "website")
    private String website;
    
    @Column(name = "code")
    private String code;
    
    // ========== FINANCIAL CONFIGURATION ==========
    @Column(name = "financial_year_start", nullable = false)
    private LocalDate financialYearStart;
    
    @Column(name = "books_start", nullable = false)
    private LocalDate booksStart;
    
    // ========== CURRENCY CONFIGURATION ==========
    @Column(name = "currency_symbol")
    private String currencySymbol = "₹";
    
    @Column(name = "currency_formal_name")
    private String currencyFormalName = "INR";
    
    @Column(name = "currency_decimal_places")
    private Integer currencyDecimalPlaces = 2;
    
    // ========== GST CONFIGURATION (INDIA) ==========
    @Column(name = "gst_applicable_date")
    private LocalDate gstApplicableDate;
    
    @Column(name = "gst_state")
    private String gstState;
    
    @Column(name = "gst_type")
    private String gstType;
    
    @Column(name = "gstin")
    private String gstin;
    
    @Column(name = "gst_freezone")
    private Boolean gstFreezone = false;
    
    @Column(name = "gst_einvoice_applicable")
    private Boolean gstEInvoiceApplicable = false;
    
    @Column(name = "gst_eway_bill_applicable")
    private Boolean gstEWayBillApplicable = false;
    
    // ========== VAT CONFIGURATION (GCC) ==========
    @Column(name = "vat_emirate")
    private String vatEmirate;
    
    @Column(name = "vat_applicable_date")
    private LocalDate vatApplicableDate;
    
    @Column(name = "vat_registration_number")
    private String vatRegistrationNumber;
    
    @Column(name = "vat_account_id")
    private String vatAccountId;
    
    @Column(name = "vat_freezone")
    private Boolean vatFreezone = false;
    
    // ========== FEATURE FLAGS ==========
    @Column(name = "billwise_enabled")
    private Boolean billwiseEnabled = true;
    
    @Column(name = "costcentre_enabled")
    private Boolean costcentreEnabled = false;
    
    @Column(name = "batch_enabled")
    private Boolean batchEnabled = false;
    
    @Column(name = "use_discount_column")
    private Boolean useDiscountColumn = true;
    
    @Column(name = "use_actual_column")
    private Boolean useActualColumn = false;
    
    @Column(name = "payroll_enabled")
    private Boolean payrollEnabled = false;
    
    // ========== AUDIT FIELDS ==========
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== CONSTRUCTORS ==========
    public Company() {
        this.companyGuid = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.currencySymbol = "₹";
        this.currencyFormalName = "INR";
        this.currencyDecimalPlaces = 2;
        this.country = "India";
        this.billwiseEnabled = true;
        this.costcentreEnabled = false;
        this.batchEnabled = false;
        this.useDiscountColumn = true;
        this.useActualColumn = false;
        this.payrollEnabled = false;
        this.gstFreezone = false;
        this.gstEInvoiceApplicable = false;
        this.gstEWayBillApplicable = false;
        this.vatFreezone = false;
    }

    public Company(String name, String addressLine1) {
        this();
        this.name = name;
        this.addressLine1 = addressLine1;
    }


    // ========== GETTERS AND SETTERS ==========
    
    // Identity & Reference
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCompanyGuid() { return companyGuid; }
    public void setCompanyGuid(String companyGuid) { this.companyGuid = companyGuid; }

    // Basic Information
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMailingName() { return mailingName; }
    public void setMailingName(String mailingName) { this.mailingName = mailingName; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImportedFrom() { return importedFrom; }
    public void setImportedFrom(String importedFrom) { this.importedFrom = importedFrom; }

    public LocalDate getImportedDate() { return importedDate; }
    public void setImportedDate(LocalDate importedDate) { this.importedDate = importedDate; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public LocalDateTime getLastSyncDate() { return lastSyncDate; }
    public void setLastSyncDate(LocalDateTime lastSyncDate) { this.lastSyncDate = lastSyncDate; }

    // Address Details
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getAddressLine3() { return addressLine3; }
    public void setAddressLine3(String addressLine3) { this.addressLine3 = addressLine3; }

    public String getAddressLine4() { return addressLine4; }
    public void setAddressLine4(String addressLine4) { this.addressLine4 = addressLine4; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    // Contact Information
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    // Financial Configuration
    public LocalDate getFinancialYearStart() { return financialYearStart; }
    public void setFinancialYearStart(LocalDate financialYearStart) { this.financialYearStart = financialYearStart; }

    public LocalDate getBooksStart() { return booksStart; }
    public void setBooksStart(LocalDate booksStart) { this.booksStart = booksStart; }

    // Currency Configuration
    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

    public String getCurrencyFormalName() { return currencyFormalName; }
    public void setCurrencyFormalName(String currencyFormalName) { this.currencyFormalName = currencyFormalName; }

    public Integer getCurrencyDecimalPlaces() { return currencyDecimalPlaces; }
    public void setCurrencyDecimalPlaces(Integer currencyDecimalPlaces) { this.currencyDecimalPlaces = currencyDecimalPlaces; }

    // GST Configuration
    public LocalDate getGstApplicableDate() { return gstApplicableDate; }
    public void setGstApplicableDate(LocalDate gstApplicableDate) { this.gstApplicableDate = gstApplicableDate; }

    public String getGstState() { return gstState; }
    public void setGstState(String gstState) { this.gstState = gstState; }

    public String getGstType() { return gstType; }
    public void setGstType(String gstType) { this.gstType = gstType; }

    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }

    public Boolean getGstFreezone() { return gstFreezone; }
    public void setGstFreezone(Boolean gstFreezone) { this.gstFreezone = gstFreezone; }

    public Boolean getGstEInvoiceApplicable() { return gstEInvoiceApplicable; }
    public void setGstEInvoiceApplicable(Boolean gstEInvoiceApplicable) { this.gstEInvoiceApplicable = gstEInvoiceApplicable; }

    public Boolean getGstEWayBillApplicable() { return gstEWayBillApplicable; }
    public void setGstEWayBillApplicable(Boolean gstEWayBillApplicable) { this.gstEWayBillApplicable = gstEWayBillApplicable; }

    // VAT Configuration
    public String getVatEmirate() { return vatEmirate; }
    public void setVatEmirate(String vatEmirate) { this.vatEmirate = vatEmirate; }

    public LocalDate getVatApplicableDate() { return vatApplicableDate; }
    public void setVatApplicableDate(LocalDate vatApplicableDate) { this.vatApplicableDate = vatApplicableDate; }

    public String getVatRegistrationNumber() { return vatRegistrationNumber; }
    public void setVatRegistrationNumber(String vatRegistrationNumber) { this.vatRegistrationNumber = vatRegistrationNumber; }

    public String getVatAccountId() { return vatAccountId; }
    public void setVatAccountId(String vatAccountId) { this.vatAccountId = vatAccountId; }

    public Boolean getVatFreezone() { return vatFreezone; }
    public void setVatFreezone(Boolean vatFreezone) { this.vatFreezone = vatFreezone; }

    // Feature Flags
    public Boolean getBillwiseEnabled() { return billwiseEnabled; }
    public void setBillwiseEnabled(Boolean billwiseEnabled) { this.billwiseEnabled = billwiseEnabled; }

    public Boolean getCostcentreEnabled() { return costcentreEnabled; }
    public void setCostcentreEnabled(Boolean costcentreEnabled) { this.costcentreEnabled = costcentreEnabled; }

    public Boolean getBatchEnabled() { return batchEnabled; }
    public void setBatchEnabled(Boolean batchEnabled) { this.batchEnabled = batchEnabled; }

    public Boolean getUseDiscountColumn() { return useDiscountColumn; }
    public void setUseDiscountColumn(Boolean useDiscountColumn) { this.useDiscountColumn = useDiscountColumn; }

    public Boolean getUseActualColumn() { return useActualColumn; }
    public void setUseActualColumn(Boolean useActualColumn) { this.useActualColumn = useActualColumn; }

    public Boolean getPayrollEnabled() { return payrollEnabled; }
    public void setPayrollEnabled(Boolean payrollEnabled) { this.payrollEnabled = payrollEnabled; }

    // Audit Fields
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
