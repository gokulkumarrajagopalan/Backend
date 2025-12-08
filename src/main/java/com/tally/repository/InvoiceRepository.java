package com.tally.repository;

import com.tally.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByCompanyId(Long companyId);
    Page<Invoice> findByCompanyId(Long companyId, Pageable pageable);
    List<Invoice> findByCompanyIdAndStatus(Long companyId, String status);
    Page<Invoice> findByCompanyIdAndStatus(Long companyId, String status, Pageable pageable);
    List<Invoice> findByPartyId(Long partyId);
    List<Invoice> findByCompanyIdAndInvoiceDateBetween(Long companyId, LocalDate startDate, LocalDate endDate);
    List<Invoice> findByCompanyIdAndInvoiceType(Long companyId, String invoiceType);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByCompanyIdAndStatusAndDueDateBefore(Long companyId, String status, LocalDate dueDate);
    
    @Query("SELECT i FROM Invoice i WHERE i.companyId = ?1 AND i.status != 'CANCELLED'")
    List<Invoice> findActiveInvoices(Long companyId);
    
    @Query("SELECT i FROM Invoice i WHERE i.companyId = ?1 AND i.createdAt >= ?2")
    List<Invoice> findInvoicesByDateRange(Long companyId, LocalDateTime fromDate);
}
