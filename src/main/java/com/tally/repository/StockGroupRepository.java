package com.tally.repository;

import com.tally.entity.StockGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockGroupRepository extends JpaRepository<StockGroup, Long> {
    List<StockGroup> findByCmpId(Long cmpId);
    
    Optional<StockGroup> findByCmpIdAndName(Long cmpId, String name);
    
    // UPSERT: Find by reconciliation identifier (cmpId + masterId)
    Optional<StockGroup> findByCmpIdAndMasterId(Long cmpId, Long masterId);
    
    // Get max AlterID for a company
    @Query("SELECT COALESCE(MAX(s.alterId), 0) FROM StockGroup s WHERE s.cmpId = :cmpId")
    Long getMaxAlterIdForCompany(@Param("cmpId") Long cmpId);
}
