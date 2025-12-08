package com.tally.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "bank_reconciliations")
public class BankReconciliation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(name = "bank_account_id")
    private Long bankAccountId;
    
    private LocalDate reconciliationDate;
    private Double bankBalance;
    private Double accountBalance;
    private Double difference;
    private String status; // PENDING, MATCHED, COMPLETED, FAILED
    private Double outstandingCheques;
    private Double depositInTransit;
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BankReconciliation() {
        this.status = "PENDING";
        this.bankBalance = 0.0;
        this.accountBalance = 0.0;
        this.difference = 0.0;
        this.outstandingCheques = 0.0;
        this.depositInTransit = 0.0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Long getBankAccountId() { return bankAccountId; }
    public void setBankAccountId(Long bankAccountId) { this.bankAccountId = bankAccountId; }

    public LocalDate getReconciliationDate() { return reconciliationDate; }
    public void setReconciliationDate(LocalDate reconciliationDate) { this.reconciliationDate = reconciliationDate; }

    public Double getBankBalance() { return bankBalance; }
    public void setBankBalance(Double bankBalance) { this.bankBalance = bankBalance; }

    public Double getAccountBalance() { return accountBalance; }
    public void setAccountBalance(Double accountBalance) { this.accountBalance = accountBalance; }

    public Double getDifference() { return difference; }
    public void setDifference(Double difference) { this.difference = difference; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getOutstandingCheques() { return outstandingCheques; }
    public void setOutstandingCheques(Double outstandingCheques) { this.outstandingCheques = outstandingCheques; }

    public Double getDepositInTransit() { return depositInTransit; }
    public void setDepositInTransit(Double depositInTransit) { this.depositInTransit = depositInTransit; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
