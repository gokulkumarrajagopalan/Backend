package com.tally.repository;

import com.tally.entity.CostCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CostCenterRepository extends JpaRepository<CostCenter, Long> {
    Optional<CostCenter> findByCmpIdAndName(Long cmpId, String name);
    
    // UPSERT: Find by reconciliation identifier (cmpId + masterId)
    Optional<CostCenter> findByCmpIdAndMasterId(Long cmpId, Long masterId);
    
    // Get max AlterID for a company
    @Query("SELECT COALESCE(MAX(c.alterId), 0) FROM CostCenter c WHERE c.cmpId = :cmpId")
    Long getMaxAlterIdForCompany(@Param("cmpId") Long cmpId);
}
