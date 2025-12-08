package com.tally.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_masters")
public class AccountMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(nullable = false)
    private String accountName;
    
    @Column(unique = true)
    private String accountCode;
    
    private String accountType; // Asset, Liability, Capital, Income, Expense, Bank
    private String subType; // Current Asset, Fixed Asset, etc.
    private Double openingBalance;
    private Double currentBalance;
    private String natureOfAccount; // Debit or Credit (nature of balance)
    private String description;
    private Boolean active;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public AccountMaster() {
        this.currentBalance = 0.0;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public AccountMaster(Long companyId, String accountName, String accountCode, String accountType) {
        this.companyId = companyId;
        this.accountName = accountName;
        this.accountCode = accountCode;
        this.accountType = accountType;
        this.currentBalance = 0.0;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getSubType() { return subType; }
    public void setSubType(String subType) { this.subType = subType; }

    public Double getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(Double openingBalance) { this.openingBalance = openingBalance; }

    public Double getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(Double currentBalance) { this.currentBalance = currentBalance; }

    public String getNatureOfAccount() { return natureOfAccount; }
    public void setNatureOfAccount(String natureOfAccount) { this.natureOfAccount = natureOfAccount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
