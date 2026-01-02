package com.tally.repository;

import com.tally.entity.CostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CostCategoryRepository extends JpaRepository<CostCategory, Long> {
    Optional<CostCategory> findByCmpIdAndName(Long cmpId, String name);
    
    // UPSERT: Find by reconciliation identifier (cmpId + masterId)
    Optional<CostCategory> findByCmpIdAndMasterId(Long cmpId, Long masterId);
    
    // Get max AlterID for a company
    @Query("SELECT COALESCE(MAX(c.alterId), 0) FROM CostCategory c WHERE c.cmpId = :cmpId")
    Long getMaxAlterIdForCompany(@Param("cmpId") Long cmpId);
}
