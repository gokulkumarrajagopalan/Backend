package com.tally.service;

import com.tally.entity.Ledger;
import com.tally.repository.LedgerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LedgerService {
    
    @Autowired
    private LedgerRepository ledgerRepository;
    
    // ========== CRITICAL: Multi-Company Upsert Logic ==========
    public Ledger upsertLedger(Ledger ledger) {
        // Use reconciliation identifier: cmpId + masterId
        Optional<Ledger> existingLedger = ledgerRepository.findByCmpIdAndMasterId(
            ledger.getCmpId(), 
            ledger.getMasterId()
        );
        
        if (existingLedger.isPresent()) {
            // UPDATE existing ledger
            Ledger existing = existingLedger.get();
            // System.out.println("♻️ Updating Ledger: " + existing.getLedName() 
            //     + " (ID: " + existing.getLedId() + ") for Company #" + existing.getCmpId());
            
            // Update Tally identifiers
            existing.setMasterId(ledger.getMasterId());
            existing.setAlterId(ledger.getAlterId());
            existing.setGuid(ledger.getGuid()); // Company-specific GUID
            
            // Update basic information
            existing.setLedCode(ledger.getLedCode());
            existing.setLedAlias(ledger.getLedAlias());
            existing.setLedParent(ledger.getLedParent());
            existing.setLedPrimaryGroup(ledger.getLedPrimaryGroup());
            existing.setLedDescription(ledger.getLedDescription());
            existing.setLedNote(ledger.getLedNote());
            
            // Update classification
            existing.setIsRevenue(ledger.getIsRevenue());
            existing.setIsReserved(ledger.getIsReserved());
            existing.setReservedName(ledger.getReservedName());
            existing.setLastParent(ledger.getLastParent());
            
            // Update features
            existing.setLedBillwiseOn(ledger.getLedBillwiseOn());
            existing.setLedIsCostcentreOn(ledger.getLedIsCostcentreOn());
            
            // Update hierarchy
            existing.setParentLedId(ledger.getParentLedId());
            existing.setGrpId(ledger.getGrpId());
            existing.setLevelNumber(ledger.getLevelNumber());
            existing.setFullPath(ledger.getFullPath());
            existing.setParentHierarchy(ledger.getParentHierarchy());
            
            // Update contact information
            existing.setLedMailingName(ledger.getLedMailingName());
            existing.setLedAddress1(ledger.getLedAddress1());
            existing.setLedAddress2(ledger.getLedAddress2());
            existing.setLedAddress3(ledger.getLedAddress3());
            existing.setLedAddress4(ledger.getLedAddress4());
            existing.setLedState(ledger.getLedState());
            existing.setLedCountry(ledger.getLedCountry());
            existing.setLedPincode(ledger.getLedPincode());
            existing.setLedContact(ledger.getLedContact());
            existing.setLedPhone(ledger.getLedPhone());
            existing.setLedCountryIsdCode(ledger.getLedCountryIsdCode());
            existing.setLedMobile(ledger.getLedMobile());
            existing.setLedEmail(ledger.getLedEmail());
            existing.setLedWebsite(ledger.getLedWebsite());
            
            // Update financial information
            existing.setLedOpeningBalance(ledger.getLedOpeningBalance());
            existing.setCurrencyName(ledger.getCurrencyName());
            existing.setIncomeTaxNumber(ledger.getIncomeTaxNumber());
            
            // Update GST configuration
            existing.setGstApplicable(ledger.getGstApplicable());
            existing.setGstRegistrationType(ledger.getGstRegistrationType());
            existing.setGstRegistrationDate(ledger.getGstRegistrationDate());
            existing.setGstGstin(ledger.getGstGstin());
            existing.setGstIsFreezone(ledger.getGstIsFreezone());
            existing.setGstState(ledger.getGstState());
            existing.setGstPlaceOfSupply(ledger.getGstPlaceOfSupply());
            existing.setGstTransporterId(ledger.getGstTransporterId());
            existing.setGstIsOtherTerritoryAssessee(ledger.getGstIsOtherTerritoryAssessee());
            existing.setGstConsiderPurchaseForExport(ledger.getGstConsiderPurchaseForExport());
            existing.setGstIsTransporter(ledger.getGstIsTransporter());
            existing.setGstIsCommonParty(ledger.getGstIsCommonParty());
            
            // Update VAT configuration
            existing.setVatApplicable(ledger.getVatApplicable());
            existing.setVatRegistrationType(ledger.getVatRegistrationType());
            existing.setVatRegistrationDate(ledger.getVatRegistrationDate());
            existing.setVatTinNumber(ledger.getVatTinNumber());
            existing.setVatIsFreezone(ledger.getVatIsFreezone());
            
            // Update multilingual support
            existing.setLanguageId(ledger.getLanguageId());
            existing.setAlternateNames(ledger.getAlternateNames());
            
            // Update status & metadata
            existing.setIsActive(ledger.getIsActive());
            existing.setIsDeleted(ledger.getIsDeleted());
            existing.setSyncStatus("SYNCED");
            existing.setLastSyncDate(LocalDateTime.now());
            existing.setMailingDetailsApplicableFrom(ledger.getMailingDetailsApplicableFrom());
            existing.setGstDetailsApplicableFrom(ledger.getGstDetailsApplicableFrom());
            
            return ledgerRepository.save(existing);
        } else {
            // CREATE new ledger for this company
            // System.out.println("➕ Creating New Ledger: " + ledger.getLedName() 
            //     + " for Company #" + ledger.getCmpId());
            ledger.setSyncStatus("SYNCED");
            ledger.setLastSyncDate(LocalDateTime.now());
            return createLedger(ledger);
        }
    }
    
    // ========== BULK SYNC FROM TALLY ==========
    public List<Ledger> syncLedgersFromTally(List<Ledger> ledgers) {
        System.out.println("🔄 Starting bulk sync for " + ledgers.size() + " ledgers...");
        return ledgers.stream()
            .map(this::upsertLedger)
            .toList();
    }
    
    // ========== BASIC CRUD OPERATIONS ==========
    public List<Ledger> getAllLedgers() {
        return ledgerRepository.findAll();
    }
    
    public Optional<Ledger> getLedgerById(Long ledId) {
        return ledgerRepository.findById(ledId);
    }
    
    public Optional<Ledger> getLedgerByMasterId(Long masterId) {
        return ledgerRepository.findByMasterId(masterId);
    }
    
    public Optional<Ledger> getLedgerByGuid(String guid) {
        return ledgerRepository.findByGuid(guid);
    }
    
    public Ledger createLedger(Ledger ledger) {
        if (ledger.getSyncStatus() == null) {
            ledger.setSyncStatus("PENDING");
        }
        return ledgerRepository.save(ledger);
    }
    
    public Ledger updateLedger(Long ledId, Ledger ledgerDetails) {
        Ledger ledger = ledgerRepository.findById(ledId)
                .orElseThrow(() -> new RuntimeException("Ledger not found with ID: " + ledId));
        
        // Update all fields
        ledger.setLedName(ledgerDetails.getLedName());
        ledger.setLedCode(ledgerDetails.getLedCode());
        ledger.setLedAlias(ledgerDetails.getLedAlias());
        ledger.setLedParent(ledgerDetails.getLedParent());
        ledger.setLedDescription(ledgerDetails.getLedDescription());
        ledger.setLedNote(ledgerDetails.getLedNote());
        ledger.setGrpId(ledgerDetails.getGrpId());
        ledger.setLedOpeningBalance(ledgerDetails.getLedOpeningBalance());
        
        // Update contact information
        ledger.setLedMailingName(ledgerDetails.getLedMailingName());
        ledger.setLedAddress1(ledgerDetails.getLedAddress1());
        ledger.setLedAddress2(ledgerDetails.getLedAddress2());
        ledger.setLedState(ledgerDetails.getLedState());
        ledger.setLedCountry(ledgerDetails.getLedCountry());
        ledger.setLedPincode(ledgerDetails.getLedPincode());
        ledger.setLedMobile(ledgerDetails.getLedMobile());
        ledger.setLedEmail(ledgerDetails.getLedEmail());
        
        // Update GST
        ledger.setGstApplicable(ledgerDetails.getGstApplicable());
        ledger.setGstGstin(ledgerDetails.getGstGstin());
        ledger.setGstState(ledgerDetails.getGstState());
        
        return ledgerRepository.save(ledger);
    }
    
    public void deleteLedger(Long ledId) {
        Ledger ledger = ledgerRepository.findById(ledId)
                .orElseThrow(() -> new RuntimeException("Ledger not found with ID: " + ledId));
        ledger.setIsDeleted(true);
        ledger.setIsActive(false);
        ledgerRepository.save(ledger);
    }
    
    public void hardDeleteLedger(Long ledId) {
        ledgerRepository.deleteById(ledId);
    }
    
    // ========== COMPANY-SPECIFIC QUERIES ==========
    public List<Ledger> getLedgersByCompany(Long cmpId) {
        return ledgerRepository.findByCmpId(cmpId);
    }
    
    public List<Ledger> getActiveLedgersByCompany(Long cmpId) {
        return ledgerRepository.findActiveLedgersByCompany(cmpId);
    }
    
    // ========== GROUP-BASED QUERIES ==========
    public List<Ledger> getLedgersByGroup(Long grpId) {
        return ledgerRepository.findByGrpId(grpId);
    }
    
    public List<Ledger> getLedgersByCompanyAndGroup(Long cmpId, Long grpId) {
        return ledgerRepository.findByCmpIdAndGrpId(cmpId, grpId);
    }
    
    // ========== CLASSIFICATION QUERIES ==========
    public List<Ledger> getCustomers(Long cmpId) {
        return ledgerRepository.findByCmpIdAndLedPrimaryGroup(cmpId, "Sundry Debtors");
    }
    
    public List<Ledger> getSuppliers(Long cmpId) {
        return ledgerRepository.findByCmpIdAndLedPrimaryGroup(cmpId, "Sundry Creditors");
    }
    
    public List<Ledger> getBankAccounts(Long cmpId) {
        return ledgerRepository.findByCmpIdAndLedPrimaryGroup(cmpId, "Bank Accounts");
    }
    
    public List<Ledger> getCashAccounts(Long cmpId) {
        return ledgerRepository.findByCmpIdAndLedPrimaryGroup(cmpId, "Cash-in-Hand");
    }
    
    // ========== SEARCH & FILTER ==========
    public List<Ledger> searchLedgers(Long cmpId, String searchTerm) {
        return ledgerRepository.searchByName(cmpId, searchTerm);
    }
    
    // ========== GST QUERIES ==========
    public List<Ledger> getGstRegisteredLedgers(Long cmpId) {
        return ledgerRepository.findGstRegisteredLedgers(cmpId);
    }
    
    public Optional<Ledger> getLedgerByGstin(String gstGstin) {
        return ledgerRepository.findByGstGstin(gstGstin);
    }
    
    // ========== CONTACT QUERIES ==========
    public Optional<Ledger> getLedgerByEmail(String email) {
        return ledgerRepository.findByLedEmail(email);
    }
    
    public Optional<Ledger> getLedgerByMobile(String mobile) {
        return ledgerRepository.findByLedMobile(mobile);
    }
    
    // ========== STATISTICS ==========
    public BigDecimal getTotalOpeningBalance(Long cmpId) {
        return ledgerRepository.findActiveLedgersByCompany(cmpId).stream()
                .map(Ledger::getLedOpeningBalance)
                .filter(balance -> balance != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public long getActiveLedgersCount(Long cmpId) {
        return ledgerRepository.findActiveLedgersByCompany(cmpId).size();
    }
}
