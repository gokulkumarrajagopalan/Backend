package com.tally.service;

import com.tally.entity.Ledger;
import com.tally.repository.LedgerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LedgerService {
    
    @Autowired
    private LedgerRepository ledgerRepository;
    
    public List<Ledger> getAllLedgers() {
        return ledgerRepository.findAll();
    }
    
    public Optional<Ledger> getLedgerById(Long id) {
        return ledgerRepository.findById(id);
    }
    
    public Ledger createLedger(Ledger ledger) {
        return ledgerRepository.save(ledger);
    }
    
    public Ledger updateLedger(Long id, Ledger ledger) {
        ledger.setId(id);
        return ledgerRepository.save(ledger);
    }
    
    public void deleteLedger(Long id) {
        ledgerRepository.deleteById(id);
    }
    
    public List<Ledger> getLedgersByGroupId(Long groupId) {
        return ledgerRepository.findByGroupId(groupId);
    }
    
    public Double getTotalBalance() {
        return ledgerRepository.findAll().stream()
                .mapToDouble(Ledger::getBalance)
                .sum();
    }
}
