package com.tally.repository;

import com.tally.entity.CompanySyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CompanySyncStatusRepository extends JpaRepository<CompanySyncStatus, Long> {
    
    Optional<CompanySyncStatus> findByCmpId(Long cmpId);
    
    @Query(value = "SELECT COALESCE(MAX(alter_id), 0) FROM (" +
           "SELECT alterid as alter_id FROM ledgers WHERE cmpid = :cmpId " +
           "UNION ALL SELECT alterid FROM groups WHERE cmpid = :cmpId " +
           "UNION ALL SELECT alterid FROM cost_categories WHERE cmpid = :cmpId " +
           "UNION ALL SELECT alterid FROM costcentre WHERE cmpid = :cmpId " +
           "UNION ALL SELECT alterid FROM currency WHERE cmpid = :cmpId " +
           "UNION ALL SELECT alterid FROM units WHERE cmpid = :cmpId " +
           "UNION ALL SELECT alterid FROM taxunit WHERE cmpid = :cmpId " +
           "UNION ALL SELECT alterid FROM vouchertype WHERE cmpid = :cmpId " +
           "UNION ALL SELECT alterid FROM godowns WHERE cmpid = :cmpId " +
           "UNION ALL SELECT alterid FROM stock_groups WHERE cmpid = :cmpId " +
           "UNION ALL SELECT alterid FROM stock_categories WHERE cmpid = :cmpId " +
           "UNION ALL SELECT alterid FROM stock_items WHERE cmpid = :cmpId" +
           ") all_alters", 
           nativeQuery = true)
    Long findMaxAlterIdForCompany(@Param("cmpId") Long cmpId);
}