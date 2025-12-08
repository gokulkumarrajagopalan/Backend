package com.tally.service;

import com.tally.entity.Budget;
import com.tally.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;

    public Budget createBudget(Budget budget) {
        budget.setStatus("DRAFT");
        budget.setVariance(budget.getBudgetAmount() - budget.getActualAmount());
        budget.setVarianceType(budget.getVariance() > 0 ? "FAVORABLE" : "UNFAVORABLE");
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long id, Budget budget) {
        Optional<Budget> existing = budgetRepository.findById(id);
        if (existing.isPresent()) {
            Budget b = existing.get();
            b.setBudgetName(budget.getBudgetName());
            b.setStartDate(budget.getStartDate());
            b.setEndDate(budget.getEndDate());
            b.setBudgetAmount(budget.getBudgetAmount());
            b.setVariance(budget.getBudgetAmount() - budget.getActualAmount());
            b.setVarianceType(b.getVariance() > 0 ? "FAVORABLE" : "UNFAVORABLE");
            b.setUpdatedAt(LocalDateTime.now());
            return budgetRepository.save(b);
        }
        throw new RuntimeException("Budget not found");
    }

    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    public List<Budget> getAllBudgets(Long companyId) {
        return budgetRepository.findByCompanyId(companyId);
    }

    public List<Budget> getBudgetsByStatus(Long companyId, String status) {
        return budgetRepository.findByCompanyIdAndStatus(companyId, status);
    }

    public Budget approveBudget(Long id) {
        Optional<Budget> budget = budgetRepository.findById(id);
        if (budget.isPresent()) {
            Budget b = budget.get();
            b.setStatus("APPROVED");
            b.setUpdatedAt(LocalDateTime.now());
            return budgetRepository.save(b);
        }
        throw new RuntimeException("Budget not found");
    }

    public Budget activateBudget(Long id) {
        Optional<Budget> budget = budgetRepository.findById(id);
        if (budget.isPresent()) {
            Budget b = budget.get();
            if (!b.getStatus().equals("APPROVED")) {
                throw new RuntimeException("Budget must be approved before activation");
            }
            b.setStatus("ACTIVE");
            b.setUpdatedAt(LocalDateTime.now());
            return budgetRepository.save(b);
        }
        throw new RuntimeException("Budget not found");
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }
}
