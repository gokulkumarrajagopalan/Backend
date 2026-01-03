package com.tally.controller;

import com.tally.entity.Ledger;
import com.tally.service.LedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ledgers")
public class LedgerController {
    
    @Autowired
    private LedgerService ledgerService;
    
    // ========== BASIC CRUD OPERATIONS ==========
    
    @GetMapping
    public ResponseEntity<List<Ledger>> getAllLedgers() {
        return ResponseEntity.ok(ledgerService.getAllLedgers());
    }
    
    @GetMapping("/{ledId}")
    public ResponseEntity<Ledger> getLedgerById(@PathVariable Long ledId) {
        return ledgerService.getLedgerById(ledId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/guid/{guid}")
    public ResponseEntity<Ledger> getLedgerByGuid(@PathVariable String guid) {
        return ledgerService.getLedgerByGuid(guid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Ledger> createLedger(@RequestBody Ledger ledger) {
        try {
            Ledger createdLedger = ledgerService.createLedger(ledger);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLedger);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{ledId}")
    public ResponseEntity<Ledger> updateLedger(
            @PathVariable Long ledId,
            @RequestBody Ledger ledgerDetails) {
        try {
            Ledger updatedLedger = ledgerService.updateLedger(ledId, ledgerDetails);
            return ResponseEntity.ok(updatedLedger);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{ledId}")
    public ResponseEntity<Void> deleteLedger(@PathVariable Long ledId) {
        try {
            ledgerService.deleteLedger(ledId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{ledId}/hard")
    public ResponseEntity<Void> hardDeleteLedger(@PathVariable Long ledId) {
        try {
            ledgerService.hardDeleteLedger(ledId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ========== COMPANY-SPECIFIC ENDPOINTS ==========
    
    @GetMapping("/company/{cmpId}")
    public ResponseEntity<List<Ledger>> getLedgersByCompany(@PathVariable Long cmpId) {
        return ResponseEntity.ok(ledgerService.getLedgersByCompany(cmpId));
    }
    
    @GetMapping("/company/{cmpId}/active")
    public ResponseEntity<List<Ledger>> getActiveLedgersByCompany(@PathVariable Long cmpId) {
        return ResponseEntity.ok(ledgerService.getActiveLedgersByCompany(cmpId));
    }
    
    @GetMapping("/company/{cmpId}/statistics")
    public ResponseEntity<LedgerStatistics> getCompanyStatistics(@PathVariable Long cmpId) {
        LedgerStatistics stats = new LedgerStatistics();
        stats.setTotalLedgers(ledgerService.getActiveLedgersCount(cmpId));
        stats.setTotalOpeningBalance(ledgerService.getTotalOpeningBalance(cmpId));
        return ResponseEntity.ok(stats);
    }
    
    // ========== GROUP-BASED ENDPOINTS ==========
    
    @GetMapping("/group/{grpId}")
    public ResponseEntity<List<Ledger>> getLedgersByGroup(@PathVariable Long grpId) {
        return ResponseEntity.ok(ledgerService.getLedgersByGroup(grpId));
    }
    
    @GetMapping("/company/{cmpId}/group/{grpId}")
    public ResponseEntity<List<Ledger>> getLedgersByCompanyAndGroup(
            @PathVariable Long cmpId,
            @PathVariable Long grpId) {
        return ResponseEntity.ok(ledgerService.getLedgersByCompanyAndGroup(cmpId, grpId));
    }
    
    // ========== CLASSIFICATION ENDPOINTS ==========
    
    @GetMapping("/company/{cmpId}/customers")
    public ResponseEntity<List<Ledger>> getCustomers(@PathVariable Long cmpId) {
        return ResponseEntity.ok(ledgerService.getCustomers(cmpId));
    }
    
    @GetMapping("/company/{cmpId}/suppliers")
    public ResponseEntity<List<Ledger>> getSuppliers(@PathVariable Long cmpId) {
        return ResponseEntity.ok(ledgerService.getSuppliers(cmpId));
    }
    
    @GetMapping("/company/{cmpId}/bank-accounts")
    public ResponseEntity<List<Ledger>> getBankAccounts(@PathVariable Long cmpId) {
        return ResponseEntity.ok(ledgerService.getBankAccounts(cmpId));
    }
    
    @GetMapping("/company/{cmpId}/cash-accounts")
    public ResponseEntity<List<Ledger>> getCashAccounts(@PathVariable Long cmpId) {
        return ResponseEntity.ok(ledgerService.getCashAccounts(cmpId));
    }
    
    // ========== SEARCH ENDPOINTS ==========
    
    @GetMapping("/company/{cmpId}/search")
    public ResponseEntity<List<Ledger>> searchLedgers(
            @PathVariable Long cmpId,
            @RequestParam String term) {
        return ResponseEntity.ok(ledgerService.searchLedgers(cmpId, term));
    }
    
    // ========== GST ENDPOINTS ==========
    
    @GetMapping("/company/{cmpId}/gst-registered")
    public ResponseEntity<List<Ledger>> getGstRegisteredLedgers(@PathVariable Long cmpId) {
        return ResponseEntity.ok(ledgerService.getGstRegisteredLedgers(cmpId));
    }
    
    @GetMapping("/gstin/{gstGstin}")
    public ResponseEntity<Ledger> getLedgerByGstin(@PathVariable String gstGstin) {
        return ledgerService.getLedgerByGstin(gstGstin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // ========== CONTACT ENDPOINTS ==========
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Ledger> getLedgerByEmail(@PathVariable String email) {
        return ledgerService.getLedgerByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/mobile/{mobile}")
    public ResponseEntity<Ledger> getLedgerByMobile(@PathVariable String mobile) {
        return ledgerService.getLedgerByMobile(mobile)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/master/{masterId}")
    public ResponseEntity<Ledger> updateLedgerByMasterId(
            @PathVariable Long masterId,
            @RequestBody Ledger ledger) {
        try {
            ledger.setMasterId(masterId);
            Ledger updatedLedger = ledgerService.upsertLedger(ledger);
            return ResponseEntity.ok(updatedLedger);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/master/{masterId}")
    public ResponseEntity<Void> deleteLedgerByMasterId(@PathVariable Long masterId) {
        try {
            Optional<Ledger> ledger = ledgerService.getLedgerByMasterId(masterId);
            if (ledger.isPresent()) {
                ledgerService.deleteLedger(ledger.get().getLedId());
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== TALLY SYNC ENDPOINT (CRITICAL) ==========
    
    @PostMapping("/sync")
    public ResponseEntity<java.util.Map<String, Object>> syncLedgersFromTally(@RequestBody List<Ledger> ledgers) {
        try {
            // System.out.println("📥 Received sync request for " + ledgers.size() + " ledgers");
            
            List<Ledger> syncedLedgers = ledgerService.syncLedgersFromTally(ledgers);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("totalReceived", ledgers.size());
            response.put("totalProcessed", syncedLedgers.size());
            response.put("message", "Successfully synced " + syncedLedgers.size() + " ledgers from Tally");
            
            // System.out.println("✅ Sync completed: " + syncedLedgers.size() + " ledgers processed");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Sync failed: " + e.getMessage());
            e.printStackTrace();
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("totalReceived", ledgers.size());
            response.put("totalProcessed", 0);
            response.put("message", "Sync failed: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ========== NESTED RESPONSE CLASSES ==========
    
    public static class LedgerStatistics {
        private long totalLedgers;
        private BigDecimal totalOpeningBalance;
        
        public long getTotalLedgers() { return totalLedgers; }
        public void setTotalLedgers(long totalLedgers) { this.totalLedgers = totalLedgers; }
        
        public BigDecimal getTotalOpeningBalance() { return totalOpeningBalance; }
        public void setTotalOpeningBalance(BigDecimal totalOpeningBalance) { 
            this.totalOpeningBalance = totalOpeningBalance; 
        }
    }
}
