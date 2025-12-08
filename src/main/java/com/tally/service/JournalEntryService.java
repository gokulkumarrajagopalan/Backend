package com.tally.service;

import com.tally.entity.JournalEntry;
import com.tally.repository.JournalEntryRepository;
import com.tally.repository.AccountMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;
    
    @Autowired
    private AccountMasterRepository accountMasterRepository;
    
    @Autowired
    private AccountMasterService accountMasterService;

    public JournalEntry createJournalEntry(JournalEntry entry) {
        if (entry.getVoucherNumber() == null || entry.getVoucherNumber().isEmpty()) {
            throw new RuntimeException("Voucher number is required");
        }
        if (entry.getEntryDate() == null) {
            entry.setEntryDate(LocalDate.now());
        }
        
        // Calculate totals
        Double totalDebit = 0.0;
        Double totalCredit = 0.0;
        
        if (entry.getJournalLines() != null) {
            for (JournalEntry.JournalLine line : entry.getJournalLines()) {
                totalDebit += line.getDebit() != null ? line.getDebit() : 0.0;
                totalCredit += line.getCredit() != null ? line.getCredit() : 0.0;
            }
        }
        
        // Validate debit equals credit
        if (Math.abs(totalDebit - totalCredit) > 0.01) {
            throw new RuntimeException("Debit and Credit totals must match. Debit: " + totalDebit + ", Credit: " + totalCredit);
        }
        
        entry.setTotalDebit(totalDebit);
        entry.setTotalCredit(totalCredit);
        entry.setStatus("Draft");
        entry.setCreatedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());
        
        return journalEntryRepository.save(entry);
    }

    public Optional<JournalEntry> getJournalEntryById(Long id) {
        return journalEntryRepository.findById(id);
    }

    public List<JournalEntry> getEntriesByCompany(Long companyId) {
        return journalEntryRepository.findByCompanyId(companyId);
    }

    public List<JournalEntry> getEntriesByDateRange(Long companyId, LocalDate startDate, LocalDate endDate) {
        return journalEntryRepository.findByCompanyIdAndEntryDateBetween(companyId, startDate, endDate);
    }

    public List<JournalEntry> getEntriesByStatus(Long companyId, String status) {
        return journalEntryRepository.findByCompanyIdAndStatus(companyId, status);
    }

    public List<JournalEntry> getEntriesByDate(Long companyId, LocalDate date) {
        return journalEntryRepository.findByCompanyIdAndEntryDate(companyId, date);
    }

    public JournalEntry updateJournalEntry(Long id, JournalEntry entry) {
        Optional<JournalEntry> existing = journalEntryRepository.findById(id);
        if (existing.isPresent()) {
            JournalEntry je = existing.get();
            
            // Only allow updates if status is Draft
            if (!"Draft".equalsIgnoreCase(je.getStatus())) {
                throw new RuntimeException("Cannot update posted or cancelled entries");
            }
            
            if (entry.getDescription() != null) je.setDescription(entry.getDescription());
            if (entry.getNarration() != null) je.setNarration(entry.getNarration());
            if (entry.getJournalLines() != null) je.setJournalLines(entry.getJournalLines());
            
            // Recalculate totals
            Double totalDebit = 0.0;
            Double totalCredit = 0.0;
            
            for (JournalEntry.JournalLine line : je.getJournalLines()) {
                totalDebit += line.getDebit() != null ? line.getDebit() : 0.0;
                totalCredit += line.getCredit() != null ? line.getCredit() : 0.0;
            }
            
            if (Math.abs(totalDebit - totalCredit) > 0.01) {
                throw new RuntimeException("Debit and Credit totals must match");
            }
            
            je.setTotalDebit(totalDebit);
            je.setTotalCredit(totalCredit);
            je.setUpdatedAt(LocalDateTime.now());
            
            return journalEntryRepository.save(je);
        }
        throw new RuntimeException("Journal Entry not found");
    }

    public JournalEntry postJournalEntry(Long id) {
        Optional<JournalEntry> entry = journalEntryRepository.findById(id);
        if (entry.isPresent()) {
            JournalEntry je = entry.get();
            
            if (!"Draft".equalsIgnoreCase(je.getStatus())) {
                throw new RuntimeException("Only draft entries can be posted");
            }
            
            // Update account balances
            if (je.getJournalLines() != null) {
                for (JournalEntry.JournalLine line : je.getJournalLines()) {
                    if (line.getDebit() > 0) {
                        accountMasterService.updateAccountBalance(line.getAccountId(), line.getDebit(), "debit");
                    }
                    if (line.getCredit() > 0) {
                        accountMasterService.updateAccountBalance(line.getAccountId(), line.getCredit(), "credit");
                    }
                }
            }
            
            je.setStatus("Posted");
            je.setUpdatedAt(LocalDateTime.now());
            return journalEntryRepository.save(je);
        }
        throw new RuntimeException("Journal Entry not found");
    }

    public void deleteJournalEntry(Long id) {
        Optional<JournalEntry> entry = journalEntryRepository.findById(id);
        if (entry.isPresent()) {
            if (!"Draft".equalsIgnoreCase(entry.get().getStatus())) {
                throw new RuntimeException("Cannot delete posted or cancelled entries");
            }
            journalEntryRepository.deleteById(id);
        }
    }

    public Optional<JournalEntry> findByVoucherNumber(Long companyId, String voucherNumber) {
        return journalEntryRepository.findByCompanyIdAndVoucherNumber(companyId, voucherNumber);
    }
}
