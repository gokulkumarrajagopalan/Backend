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
@CrossOrigin(origins = "*")
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
    
    // ========== TALLY SYNC ENDPOINT (CRITICAL) ==========
    
    @PostMapping("/sync")
    public ResponseEntity<SyncResponse> syncLedgersFromTally(@RequestBody List<Ledger> ledgers) {
        try {
            // System.out.println("📥 Received sync request for " + ledgers.size() + " ledgers");
            
            List<Ledger> syncedLedgers = ledgerService.syncLedgersFromTally(ledgers);
            
            SyncResponse response = new SyncResponse();
            response.setSuccess(true);
            response.setTotalReceived(ledgers.size());
            response.setTotalProcessed(syncedLedgers.size());
            response.setMessage("Successfully synced " + syncedLedgers.size() + " ledgers from Tally");
            
            // System.out.println("✅ Sync completed: " + syncedLedgers.size() + " ledgers processed");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Sync failed: " + e.getMessage());
            e.printStackTrace();
            
            SyncResponse response = new SyncResponse();
            response.setSuccess(false);
            response.setTotalReceived(ledgers.size());
            response.setTotalProcessed(0);
            response.setMessage("Sync failed: " + e.getMessage());
            
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
    
    public static class SyncResponse {
        private boolean success;
        private int totalReceived;
        private int totalProcessed;
        private String message;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public int getTotalReceived() { return totalReceived; }
        public void setTotalReceived(int totalReceived) { this.totalReceived = totalReceived; }
        
        public int getTotalProcessed() { return totalProcessed; }
        public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
