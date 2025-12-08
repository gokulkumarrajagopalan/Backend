package com.tally.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    private String voucherNumber;
    private LocalDate entryDate;
    private String description;
    private String narration;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "journal_entry_id")
    private List<JournalLine> journalLines;
    
    private Double totalDebit;
    private Double totalCredit;
    private String status; // Draft, Posted, Cancelled
    private String referenceNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public JournalEntry() {
        this.totalDebit = 0.0;
        this.totalCredit = 0.0;
        this.status = "Draft";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public JournalEntry(Long companyId, String voucherNumber, LocalDate entryDate, String description) {
        this.companyId = companyId;
        this.voucherNumber = voucherNumber;
        this.entryDate = entryDate;
        this.description = description;
        this.totalDebit = 0.0;
        this.totalCredit = 0.0;
        this.status = "Draft";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getVoucherNumber() { return voucherNumber; }
    public void setVoucherNumber(String voucherNumber) { this.voucherNumber = voucherNumber; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }

    public List<JournalLine> getJournalLines() { return journalLines; }
    public void setJournalLines(List<JournalLine> journalLines) { this.journalLines = journalLines; }

    public Double getTotalDebit() { return totalDebit; }
    public void setTotalDebit(Double totalDebit) { this.totalDebit = totalDebit; }

    public Double getTotalCredit() { return totalCredit; }
    public void setTotalCredit(Double totalCredit) { this.totalCredit = totalCredit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Inner class for Journal Line
    @Entity
    @Table(name = "journal_lines")
    public static class JournalLine {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @Column(name = "account_id")
        private Long accountId;
        
        private String accountName;
        private String particulars;
        private Double debit;
        private Double credit;

        public JournalLine() {}

        public JournalLine(Long accountId, String accountName, Double debit, Double credit) {
            this.accountId = accountId;
            this.accountName = accountName;
            this.debit = debit != null ? debit : 0.0;
            this.credit = credit != null ? credit : 0.0;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getAccountId() { return accountId; }
        public void setAccountId(Long accountId) { this.accountId = accountId; }

        public String getAccountName() { return accountName; }
        public void setAccountName(String accountName) { this.accountName = accountName; }

        public String getParticulars() { return particulars; }
        public void setParticulars(String particulars) { this.particulars = particulars; }

        public Double getDebit() { return debit; }
        public void setDebit(Double debit) { this.debit = debit != null ? debit : 0.0; }

        public Double getCredit() { return credit; }
        public void setCredit(Double credit) { this.credit = credit != null ? credit : 0.0; }
    }
}
