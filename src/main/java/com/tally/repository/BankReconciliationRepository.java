package com.tally.repository;

import com.tally.entity.BankReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankReconciliationRepository extends JpaRepository<BankReconciliation, Long> {
    List<BankReconciliation> findByCompanyId(Long companyId);
    List<BankReconciliation> findByCompanyIdAndStatus(Long companyId, String status);
    List<BankReconciliation> findByBankAccountId(Long bankAccountId);
    List<BankReconciliation> findByCompanyIdAndReconciliationDateBetween(Long companyId, LocalDate startDate, LocalDate endDate);
    Optional<BankReconciliation> findByBankAccountIdAndReconciliationDate(Long bankAccountId, LocalDate reconciliationDate);
}
