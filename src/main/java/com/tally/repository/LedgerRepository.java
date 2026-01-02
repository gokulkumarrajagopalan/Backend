package com.tally.repository;

import com.tally.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    
    // ========== CRITICAL: Multi-Company Upsert ==========
    Optional<Ledger> findByCmpIdAndLedName(Long cmpId, String ledName);
    
    // ========== TALLY IDENTIFIERS ==========
    Optional<Ledger> findByGuid(String guid);
    Optional<Ledger> findByMasterId(Long masterId);
    List<Ledger> findByAlterId(Long alterId);
    
    // UPSERT: Find by reconciliation identifier (cmpId + masterId)
    Optional<Ledger> findByCmpIdAndMasterId(Long cmpId, Long masterId);
    
    // ========== COMPANY-SPECIFIC QUERIES ==========
    List<Ledger> findByCmpId(Long cmpId);
    List<Ledger> findByCmpIdAndIsActiveAndIsDeleted(Long cmpId, Boolean isActive, Boolean isDeleted);
    
    @Query("SELECT l FROM Ledger l WHERE l.cmpId = :cmpId AND l.isActive = true AND l.isDeleted = false")
    List<Ledger> findActiveLedgersByCompany(@Param("cmpId") Long cmpId);
    
    // ========== GROUP & HIERARCHY QUERIES ==========
    List<Ledger> findByGrpId(Long grpId);
    List<Ledger> findByCmpIdAndGrpId(Long cmpId, Long grpId);
    List<Ledger> findByLedParent(String ledParent);
    List<Ledger> findByParentLedId(Long parentLedId);
    
    // ========== CLASSIFICATION QUERIES ==========
    List<Ledger> findByCmpIdAndIsRevenue(Long cmpId, Boolean isRevenue);
    List<Ledger> findByLedPrimaryGroup(String ledPrimaryGroup);
    List<Ledger> findByCmpIdAndLedPrimaryGroup(Long cmpId, String ledPrimaryGroup);
    
    // ========== SEARCH & FILTER ==========
    @Query("SELECT l FROM Ledger l WHERE l.cmpId = :cmpId AND " +
           "(LOWER(l.ledName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.ledCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.ledAlias) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Ledger> searchByName(@Param("cmpId") Long cmpId, @Param("searchTerm") String searchTerm);
    
    // ========== CONTACT QUERIES ==========
    Optional<Ledger> findByLedEmail(String ledEmail);
    Optional<Ledger> findByLedMobile(String ledMobile);
    List<Ledger> findByLedState(String ledState);
    List<Ledger> findByCmpIdAndLedCountry(Long cmpId, String ledCountry);
    
    // ========== GST QUERIES ==========
    Optional<Ledger> findByGstGstin(String gstGstin);
    List<Ledger> findByCmpIdAndGstApplicable(Long cmpId, Boolean gstApplicable);
    List<Ledger> findByCmpIdAndGstState(Long cmpId, String gstState);
    
    @Query("SELECT l FROM Ledger l WHERE l.cmpId = :cmpId AND l.gstApplicable = true " +
           "AND l.gstGstin IS NOT NULL AND l.isActive = true")
    List<Ledger> findGstRegisteredLedgers(@Param("cmpId") Long cmpId);
    
    // ========== VAT QUERIES ==========
    Optional<Ledger> findByVatTinNumber(String vatTinNumber);
    List<Ledger> findByCmpIdAndVatApplicable(Long cmpId, Boolean vatApplicable);
    
    // ========== SPECIAL PURPOSE QUERIES ==========
    @Query("SELECT l FROM Ledger l WHERE l.cmpId = :cmpId AND l.ledBillwiseOn = true")
    List<Ledger> findBillwiseLedgers(@Param("cmpId") Long cmpId);
    
    @Query("SELECT l FROM Ledger l WHERE l.cmpId = :cmpId AND l.ledIsCostcentreOn = true")
    List<Ledger> findCostCentreLedgers(@Param("cmpId") Long cmpId);
    
    // ========== SYNC STATUS ==========
    List<Ledger> findBySyncStatus(String syncStatus);
    List<Ledger> findByCmpIdAndSyncStatus(Long cmpId, String syncStatus);
    
    // Get max AlterID for a company
    @Query("SELECT COALESCE(MAX(l.alterId), 0) FROM Ledger l WHERE l.cmpId = :cmpId")
    Long getMaxAlterIdForCompany(@Param("cmpId") Long cmpId);
}
