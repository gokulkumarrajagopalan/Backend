package com.tally.repository;

import com.tally.entity.StockCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockCategoryRepository extends JpaRepository<StockCategory, Long> {
    List<StockCategory> findByCmpId(Long cmpId);
    
    Optional<StockCategory> findByCmpIdAndName(Long cmpId, String name);
    
    // UPSERT: Find by reconciliation identifier (cmpId + masterId)
    Optional<StockCategory> findByCmpIdAndMasterId(Long cmpId, Long masterId);
    
    // Get max AlterID for a company
    @Query("SELECT COALESCE(MAX(s.alterId), 0) FROM StockCategory s WHERE s.cmpId = :cmpId")
    Long getMaxAlterIdForCompany(@Param("cmpId") Long cmpId);
}
