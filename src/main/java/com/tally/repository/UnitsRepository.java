
package com.tally.repository;

import com.tally.entity.Units;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitsRepository extends JpaRepository<Units, Long> {
    List<Units> findByCmpId(Long cmpId);
    
    Optional<Units> findByCmpIdAndUnitName(long cmpId, String unitName);
    
    // UPSERT: Find by reconciliation identifier (cmpId + masterId)
    Optional<Units> findByCmpIdAndMasterId(Long cmpId, Long masterId);
    
    // Get max AlterID for a company
    @Query("SELECT COALESCE(MAX(u.alterId), 0) FROM Units u WHERE u.cmpId = :cmpId")
    Long getMaxAlterIdForCompany(@Param("cmpId") Long cmpId);
}
