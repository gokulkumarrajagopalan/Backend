package com.tally.service;

import com.tally.entity.BankReconciliation;
import com.tally.repository.BankReconciliationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BankReconciliationService {
    @Autowired
    private BankReconciliationRepository bankReconciliationRepository;

    public BankReconciliation createReconciliation(BankReconciliation reconciliation) {
        reconciliation.setDifference(Math.abs(reconciliation.getBankBalance() - reconciliation.getAccountBalance()));
        reconciliation.setStatus("PENDING");
        reconciliation.setCreatedAt(LocalDateTime.now());
        reconciliation.setUpdatedAt(LocalDateTime.now());
        return bankReconciliationRepository.save(reconciliation);
    }

    public BankReconciliation updateReconciliation(Long id, BankReconciliation reconciliation) {
        Optional<BankReconciliation> existing = bankReconciliationRepository.findById(id);
        if (existing.isPresent()) {
            BankReconciliation rec = existing.get();
            rec.setBankBalance(reconciliation.getBankBalance());
            rec.setAccountBalance(reconciliation.getAccountBalance());
            rec.setOutstandingCheques(reconciliation.getOutstandingCheques());
            rec.setDepositInTransit(reconciliation.getDepositInTransit());
            rec.setDifference(Math.abs(reconciliation.getBankBalance() - reconciliation.getAccountBalance()));
            rec.setUpdatedAt(LocalDateTime.now());
            return bankReconciliationRepository.save(rec);
        }
        throw new RuntimeException("Reconciliation not found");
    }

    public Optional<BankReconciliation> getReconciliationById(Long id) {
        return bankReconciliationRepository.findById(id);
    }

    public List<BankReconciliation> getAllReconciliations(Long companyId) {
        return bankReconciliationRepository.findByCompanyId(companyId);
    }

    public List<BankReconciliation> getReconciliationsByStatus(Long companyId, String status) {
        return bankReconciliationRepository.findByCompanyIdAndStatus(companyId, status);
    }

    public BankReconciliation completeReconciliation(Long id) {
        Optional<BankReconciliation> reconciliation = bankReconciliationRepository.findById(id);
        if (reconciliation.isPresent()) {
            BankReconciliation rec = reconciliation.get();
            Double adjustedBalance = rec.getAccountBalance() + rec.getOutstandingCheques() - rec.getDepositInTransit();
            if (Math.abs(adjustedBalance - rec.getBankBalance()) < 0.01) {
                rec.setStatus("COMPLETED");
            } else {
                rec.setStatus("FAILED");
            }
            rec.setUpdatedAt(LocalDateTime.now());
            return bankReconciliationRepository.save(rec);
        }
        throw new RuntimeException("Reconciliation not found");
    }

    public void deleteReconciliation(Long id) {
        bankReconciliationRepository.deleteById(id);
    }
}
