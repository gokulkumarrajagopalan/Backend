package com.tally.repository;

import com.tally.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByCompanyId(Long companyId);
    List<Budget> findByCompanyIdAndStatus(Long companyId, String status);
    List<Budget> findByAccountId(Long accountId);
    List<Budget> findByCompanyIdAndStartDateBetween(Long companyId, LocalDate startDate, LocalDate endDate);
    List<Budget> findByCompanyIdAndPeriod(Long companyId, String period);
    Optional<Budget> findByBudgetName(String budgetName);
}
