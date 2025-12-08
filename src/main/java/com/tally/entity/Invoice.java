package com.tally.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(unique = true, nullable = false)
    private String invoiceNumber;
    
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    
    @Column(name = "party_id")
    private Long partyId;
    
    @Column(nullable = false)
    private String status; // DRAFT, SENT, PAID, OVERDUE, CANCELLED
    
    private Double taxableAmount;
    private Double taxAmount;
    private Double totalAmount;
    private String paymentTerms; // NET30, NET60, DUE_ON_RECEIPT
    private String paymentMethod; // CASH, CHEQUE, BANK_TRANSFER, CREDIT
    private String invoiceType; // SALES, PURCHASE, SERVICE
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "invoice_id")
    private List<InvoiceLineItem> lineItems;
    
    private String notes;
    private Double paidAmount;
    private LocalDate paidDate;
    private String referenceNumber;
    private Boolean taxIncluded;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Invoice() {
        this.status = "DRAFT";
        this.taxableAmount = 0.0;
        this.taxAmount = 0.0;
        this.totalAmount = 0.0;
        this.paidAmount = 0.0;
        this.taxIncluded = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Long getPartyId() { return partyId; }
    public void setPartyId(Long partyId) { this.partyId = partyId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTaxableAmount() { return taxableAmount; }
    public void setTaxableAmount(Double taxableAmount) { this.taxableAmount = taxableAmount; }

    public Double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getInvoiceType() { return invoiceType; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<InvoiceLineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<InvoiceLineItem> lineItems) { this.lineItems = lineItems; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }

    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public Boolean getTaxIncluded() { return taxIncluded; }
    public void setTaxIncluded(Boolean taxIncluded) { this.taxIncluded = taxIncluded; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

