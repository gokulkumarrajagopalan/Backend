package com.tally.service;

import com.tally.entity.AccountMaster;
import com.tally.repository.AccountMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountMasterService {
    @Autowired
    private AccountMasterRepository accountMasterRepository;

    public AccountMaster createAccount(AccountMaster account) {
        if (account.getAccountName() == null || account.getAccountName().isEmpty()) {
            throw new RuntimeException("Account name is required");
        }
        if (account.getAccountType() == null || account.getAccountType().isEmpty()) {
            throw new RuntimeException("Account type is required");
        }
        account.setCurrentBalance(account.getOpeningBalance() != null ? account.getOpeningBalance() : 0.0);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        return accountMasterRepository.save(account);
    }

    public Optional<AccountMaster> getAccountById(Long id) {
        return accountMasterRepository.findById(id);
    }

    public List<AccountMaster> getAllAccounts() {
        return accountMasterRepository.findAll();
    }

    public List<AccountMaster> getAccountsByCompany(Long companyId) {
        return accountMasterRepository.findByCompanyId(companyId);
    }

    public List<AccountMaster> getAccountsByCompanyAndType(Long companyId, String accountType) {
        return accountMasterRepository.findByCompanyIdAndAccountType(companyId, accountType);
    }

    public List<AccountMaster> getActiveAccounts(Long companyId) {
        return accountMasterRepository.findByCompanyIdAndActiveTrue(companyId);
    }

    public AccountMaster updateAccount(Long id, AccountMaster account) {
        Optional<AccountMaster> existing = accountMasterRepository.findById(id);
        if (existing.isPresent()) {
            AccountMaster acc = existing.get();
            if (account.getAccountName() != null) acc.setAccountName(account.getAccountName());
            if (account.getAccountType() != null) acc.setAccountType(account.getAccountType());
            if (account.getSubType() != null) acc.setSubType(account.getSubType());
            if (account.getDescription() != null) acc.setDescription(account.getDescription());
            if (account.getNatureOfAccount() != null) acc.setNatureOfAccount(account.getNatureOfAccount());
            if (account.getActive() != null) acc.setActive(account.getActive());
            acc.setUpdatedAt(LocalDateTime.now());
            return accountMasterRepository.save(acc);
        }
        throw new RuntimeException("Account not found");
    }

    public void updateAccountBalance(Long accountId, Double amount, String type) {
        Optional<AccountMaster> account = accountMasterRepository.findById(accountId);
        if (account.isPresent()) {
            AccountMaster acc = account.get();
            Double currentBalance = acc.getCurrentBalance();
            
            if ("debit".equalsIgnoreCase(type)) {
                acc.setCurrentBalance(currentBalance + amount);
            } else if ("credit".equalsIgnoreCase(type)) {
                acc.setCurrentBalance(currentBalance - amount);
            }
            acc.setUpdatedAt(LocalDateTime.now());
            accountMasterRepository.save(acc);
        }
    }

    public void deleteAccount(Long id) {
        accountMasterRepository.deleteById(id);
    }

    public Optional<AccountMaster> findByAccountCode(Long companyId, String accountCode) {
        return accountMasterRepository.findByCompanyIdAndAccountCode(companyId, accountCode);
    }
}
