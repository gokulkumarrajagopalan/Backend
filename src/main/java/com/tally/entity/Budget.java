package com.tally.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(nullable = false)
    private String budgetName;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private String period; // MONTHLY, QUARTERLY, ANNUAL
    private Double totalBudget;
    private String status; // DRAFT, APPROVED, ACTIVE, CLOSED
    
    @Column(name = "account_id")
    private Long accountId;
    
    private Double budgetAmount;
    private Double actualAmount;
    private Double variance;
    private String varianceType; // FAVORABLE, UNFAVORABLE
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Budget() {
        this.status = "DRAFT";
        this.totalBudget = 0.0;
        this.budgetAmount = 0.0;
        this.actualAmount = 0.0;
        this.variance = 0.0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getBudgetName() { return budgetName; }
    public void setBudgetName(String budgetName) { this.budgetName = budgetName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public Double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(Double totalBudget) { this.totalBudget = totalBudget; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public Double getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(Double budgetAmount) { this.budgetAmount = budgetAmount; }

    public Double getActualAmount() { return actualAmount; }
    public void setActualAmount(Double actualAmount) { this.actualAmount = actualAmount; }

    public Double getVariance() { return variance; }
    public void setVariance(Double variance) { this.variance = variance; }

    public String getVarianceType() { return varianceType; }
    public void setVarianceType(String varianceType) { this.varianceType = varianceType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
