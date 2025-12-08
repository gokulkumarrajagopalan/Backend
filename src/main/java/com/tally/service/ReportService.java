package com.tally.service;

import com.tally.entity.AccountMaster;
import com.tally.repository.AccountMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReportService {
    @Autowired
    private AccountMasterRepository accountMasterRepository;

    // Trial Balance Report
    public Map<String, Object> getTrialBalance(Long companyId) {
        List<AccountMaster> accounts = accountMasterRepository.findByCompanyId(companyId);
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> accounts_list = new ArrayList<>();
        Double totalDebit = 0.0;
        Double totalCredit = 0.0;
        
        for (AccountMaster account : accounts) {
            Map<String, Object> accountRow = new HashMap<>();
            accountRow.put("accountCode", account.getAccountCode());
            accountRow.put("accountName", account.getAccountName());
            accountRow.put("accountType", account.getAccountType());
            
            Double balance = account.getCurrentBalance();
            String nature = account.getNatureOfAccount();
            
            if ("Debit".equalsIgnoreCase(nature) || 
                ("Asset".equalsIgnoreCase(account.getAccountType()) && balance > 0)) {
                accountRow.put("debit", balance);
                accountRow.put("credit", 0.0);
                totalDebit += balance;
            } else {
                accountRow.put("debit", 0.0);
                accountRow.put("credit", Math.abs(balance));
                totalCredit += Math.abs(balance);
            }
            
            accounts_list.add(accountRow);
        }
        
        result.put("accounts", accounts_list);
        result.put("totalDebit", totalDebit);
        result.put("totalCredit", totalCredit);
        result.put("balanced", Math.abs(totalDebit - totalCredit) < 0.01);
        
        return result;
    }

    // Balance Sheet Report
    public Map<String, Object> getBalanceSheet(Long companyId) {
        List<AccountMaster> accounts = accountMasterRepository.findByCompanyId(companyId);
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> assets = new HashMap<>();
        Map<String, Object> liabilities = new HashMap<>();
        Map<String, Object> capital = new HashMap<>();
        
        Double assetsTotal = 0.0;
        Double liabilitiesTotal = 0.0;
        Double capitalTotal = 0.0;
        
        List<Map<String, Object>> assetsList = new ArrayList<>();
        List<Map<String, Object>> liabilitiesList = new ArrayList<>();
        List<Map<String, Object>> capitalList = new ArrayList<>();
        
        for (AccountMaster account : accounts) {
            if ("Asset".equalsIgnoreCase(account.getAccountType())) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", account.getAccountName());
                item.put("code", account.getAccountCode());
                item.put("amount", account.getCurrentBalance());
                assetsList.add(item);
                assetsTotal += account.getCurrentBalance();
            } else if ("Liability".equalsIgnoreCase(account.getAccountType())) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", account.getAccountName());
                item.put("code", account.getAccountCode());
                item.put("amount", Math.abs(account.getCurrentBalance()));
                liabilitiesList.add(item);
                liabilitiesTotal += Math.abs(account.getCurrentBalance());
            } else if ("Capital".equalsIgnoreCase(account.getAccountType())) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", account.getAccountName());
                item.put("code", account.getAccountCode());
                item.put("amount", Math.abs(account.getCurrentBalance()));
                capitalList.add(item);
                capitalTotal += Math.abs(account.getCurrentBalance());
            }
        }
        
        assets.put("details", assetsList);
        assets.put("total", assetsTotal);
        
        liabilities.put("details", liabilitiesList);
        liabilities.put("total", liabilitiesTotal);
        
        capital.put("details", capitalList);
        capital.put("total", capitalTotal);
        
        result.put("assets", assets);
        result.put("liabilities", liabilities);
        result.put("capital", capital);
        result.put("liabilitiesAndCapital", liabilitiesTotal + capitalTotal);
        result.put("balanced", Math.abs(assetsTotal - (liabilitiesTotal + capitalTotal)) < 0.01);
        
        return result;
    }

    // Profit & Loss Report
    public Map<String, Object> getProfitAndLoss(Long companyId) {
        List<AccountMaster> accounts = accountMasterRepository.findByCompanyId(companyId);
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> income = new HashMap<>();
        Map<String, Object> expenses = new HashMap<>();
        
        Double incomeTotal = 0.0;
        Double expensesTotal = 0.0;
        
        List<Map<String, Object>> incomeList = new ArrayList<>();
        List<Map<String, Object>> expensesList = new ArrayList<>();
        
        for (AccountMaster account : accounts) {
            if ("Income".equalsIgnoreCase(account.getAccountType())) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", account.getAccountName());
                item.put("code", account.getAccountCode());
                item.put("amount", Math.abs(account.getCurrentBalance()));
                incomeList.add(item);
                incomeTotal += Math.abs(account.getCurrentBalance());
            } else if ("Expense".equalsIgnoreCase(account.getAccountType())) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", account.getAccountName());
                item.put("code", account.getAccountCode());
                item.put("amount", account.getCurrentBalance());
                expensesList.add(item);
                expensesTotal += account.getCurrentBalance();
            }
        }
        
        income.put("details", incomeList);
        income.put("total", incomeTotal);
        
        expenses.put("details", expensesList);
        expenses.put("total", expensesTotal);
        
        Double netProfit = incomeTotal - expensesTotal;
        
        result.put("income", income);
        result.put("expenses", expenses);
        result.put("netProfit", netProfit);
        result.put("profitOrLoss", netProfit > 0 ? "Profit" : "Loss");
        
        return result;
    }

    // Ledger Report for specific account
    public Map<String, Object> getAccountLedger(Long accountId) {
        Optional<AccountMaster> account = accountMasterRepository.findById(accountId);
        
        Map<String, Object> result = new HashMap<>();
        if (account.isPresent()) {
            AccountMaster acc = account.get();
            result.put("accountCode", acc.getAccountCode());
            result.put("accountName", acc.getAccountName());
            result.put("accountType", acc.getAccountType());
            result.put("openingBalance", acc.getOpeningBalance());
            result.put("currentBalance", acc.getCurrentBalance());
            result.put("nature", acc.getNatureOfAccount());
        }
        return result;
    }

    // Cash Flow Report
    public Map<String, Object> getCashFlow(Long companyId) {
        List<AccountMaster> accounts = accountMasterRepository.findByCompanyId(companyId);
        
        Map<String, Object> result = new HashMap<>();
        
        Double cashIncoming = 0.0;
        Double cashOutgoing = 0.0;
        
        for (AccountMaster account : accounts) {
            if (("Receipt".equalsIgnoreCase(account.getAccountType()) || 
                 "Income".equalsIgnoreCase(account.getAccountType())) && 
                account.getCurrentBalance() > 0) {
                cashIncoming += account.getCurrentBalance();
            } else if (("Payment".equalsIgnoreCase(account.getAccountType()) || 
                       "Expense".equalsIgnoreCase(account.getAccountType())) && 
                      account.getCurrentBalance() > 0) {
                cashOutgoing += account.getCurrentBalance();
            }
        }
        
        result.put("cashIncoming", cashIncoming);
        result.put("cashOutgoing", cashOutgoing);
        result.put("netCashFlow", cashIncoming - cashOutgoing);
        
        return result;
    }
}
