package com.tally.service;

import com.tally.entity.Invoice;
import com.tally.entity.InvoiceLineItem;
import com.tally.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    public Invoice createInvoice(Invoice invoice) {
        if (invoice == null || invoice.getCompanyId() == null) {
            throw new IllegalArgumentException("Company ID is required");
        }
        
        // Calculate line items and totals
        if (invoice.getLineItems() != null && !invoice.getLineItems().isEmpty()) {
            Double totalAmount = 0.0;
            Double totalTax = 0.0;
            for (InvoiceLineItem item : invoice.getLineItems()) {
                if (item.getQuantity() == null || item.getUnitPrice() == null) {
                    throw new IllegalArgumentException("Line item quantity and unit price are required");
                }
                Double lineAmount = item.getQuantity() * item.getUnitPrice();
                item.setAmount(lineAmount);
                Double tax = lineAmount * (item.getTaxRate() != null ? item.getTaxRate() : 0) / 100;
                item.setTaxAmount(tax);
                totalAmount += lineAmount;
                totalTax += tax;
            }
            invoice.setTaxableAmount(totalAmount);
            invoice.setTaxAmount(totalTax);
            if (invoice.getTaxIncluded() == null || !invoice.getTaxIncluded()) {
                invoice.setTotalAmount(totalAmount + totalTax);
            } else {
                invoice.setTotalAmount(totalAmount);
            }
        }
        
        if (invoice.getStatus() == null) {
            invoice.setStatus("DRAFT");
        }
        if (invoice.getPaidAmount() == null) {
            invoice.setPaidAmount(0.0);
        }
        
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setUpdatedAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Long id, Invoice invoice) {
        if (id == null) {
            throw new IllegalArgumentException("Invoice ID is required");
        }
        
        Optional<Invoice> existing = invoiceRepository.findById(id);
        if (!existing.isPresent()) {
            throw new RuntimeException("Invoice not found with ID: " + id);
        }
        
        Invoice inv = existing.get();
        
        // Only allow updates to DRAFT and CANCELLED invoices
        if (!inv.getStatus().equals("DRAFT") && !inv.getStatus().equals("CANCELLED")) {
            throw new RuntimeException("Can only update invoices in DRAFT or CANCELLED status. Current status: " + inv.getStatus());
        }
        
        // Update fields
        if (invoice.getInvoiceNumber() != null) inv.setInvoiceNumber(invoice.getInvoiceNumber());
        if (invoice.getInvoiceDate() != null) inv.setInvoiceDate(invoice.getInvoiceDate());
        if (invoice.getDueDate() != null) inv.setDueDate(invoice.getDueDate());
        if (invoice.getPartyId() != null) inv.setPartyId(invoice.getPartyId());
        if (invoice.getPaymentTerms() != null) inv.setPaymentTerms(invoice.getPaymentTerms());
        if (invoice.getPaymentMethod() != null) inv.setPaymentMethod(invoice.getPaymentMethod());
        if (invoice.getDescription() != null) inv.setDescription(invoice.getDescription());
        if (invoice.getLineItems() != null) inv.setLineItems(invoice.getLineItems());
        if (invoice.getNotes() != null) inv.setNotes(invoice.getNotes());
        if (invoice.getStatus() != null) inv.setStatus(invoice.getStatus());
        
        inv.setUpdatedAt(LocalDateTime.now());
        
        // Recalculate totals if line items changed
        if (invoice.getLineItems() != null) {
            return createInvoice(inv);
        }
        
        return invoiceRepository.save(inv);
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invoice ID is required");
        }
        return invoiceRepository.findById(id);
    }

    public List<Invoice> getAllInvoices(Long companyId) {
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID is required");
        }
        return invoiceRepository.findByCompanyId(companyId);
    }
    
    public Page<Invoice> getAllInvoicesPaginated(Long companyId, Pageable pageable) {
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID is required");
        }
        return invoiceRepository.findByCompanyId(companyId, pageable);
    }

    public List<Invoice> getInvoicesByStatus(Long companyId, String status) {
        if (companyId == null || status == null) {
            throw new IllegalArgumentException("Company ID and status are required");
        }
        return invoiceRepository.findByCompanyIdAndStatus(companyId, status);
    }

    public Page<Invoice> getInvoicesByStatusPaginated(Long companyId, String status, Pageable pageable) {
        if (companyId == null || status == null) {
            throw new IllegalArgumentException("Company ID and status are required");
        }
        return invoiceRepository.findByCompanyIdAndStatus(companyId, status, pageable);
    }

    public List<Invoice> getInvoicesByDateRange(Long companyId, LocalDate startDate, LocalDate endDate) {
        if (companyId == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Company ID, start date, and end date are required");
        }
        return invoiceRepository.findByCompanyIdAndInvoiceDateBetween(companyId, startDate, endDate);
    }

    public List<Invoice> getInvoicesByParty(Long partyId) {
        if (partyId == null) {
            throw new IllegalArgumentException("Party ID is required");
        }
        return invoiceRepository.findByPartyId(partyId);
    }

    public List<Invoice> getActiveInvoices(Long companyId) {
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID is required");
        }
        return invoiceRepository.findActiveInvoices(companyId);
    }

    public List<Invoice> getOverdueInvoices(Long companyId) {
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID is required");
        }
        return invoiceRepository.findByCompanyIdAndStatusAndDueDateBefore(companyId, "SENT", LocalDate.now());
    }

    public Invoice markAsPaid(Long id, Double paidAmount, LocalDate paidDate) {
        if (id == null || paidAmount == null) {
            throw new IllegalArgumentException("Invoice ID and paid amount are required");
        }
        
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (!invoice.isPresent()) {
            throw new RuntimeException("Invoice not found with ID: " + id);
        }
        
        Invoice inv = invoice.get();
        inv.setPaidAmount(paidAmount);
        inv.setPaidDate(paidDate != null ? paidDate : LocalDate.now());
        
        if (paidAmount >= inv.getTotalAmount()) {
            inv.setStatus("PAID");
        }
        inv.setUpdatedAt(LocalDateTime.now());
        return invoiceRepository.save(inv);
    }

    public Invoice changeStatus(Long id, String status) {
        if (id == null || status == null) {
            throw new IllegalArgumentException("Invoice ID and status are required");
        }
        
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (!invoice.isPresent()) {
            throw new RuntimeException("Invoice not found with ID: " + id);
        }
        
        Invoice inv = invoice.get();
        inv.setStatus(status);
        inv.setUpdatedAt(LocalDateTime.now());
        return invoiceRepository.save(inv);
    }

    public Double getTotalRevenueByCompany(Long companyId) {
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID is required");
        }
        List<Invoice> invoices = invoiceRepository.findByCompanyIdAndStatus(companyId, "PAID");
        return invoices.stream()
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

    public void deleteInvoice(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invoice ID is required");
        }
        invoiceRepository.deleteById(id);
    }
}
