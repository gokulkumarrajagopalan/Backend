package com.tally.repository;

import com.tally.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByCompanyId(Long companyId);
    Optional<JournalEntry> findByCompanyIdAndVoucherNumber(Long companyId, String voucherNumber);
    List<JournalEntry> findByCompanyIdAndEntryDateBetween(Long companyId, LocalDate startDate, LocalDate endDate);
    List<JournalEntry> findByCompanyIdAndStatus(Long companyId, String status);
    List<JournalEntry> findByCompanyIdAndEntryDate(Long companyId, LocalDate entryDate);
}
