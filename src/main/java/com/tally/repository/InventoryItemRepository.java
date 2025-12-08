package com.tally.repository;

import com.tally.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByCompanyId(Long companyId);
    List<InventoryItem> findByCompanyIdAndCategory(Long companyId, String category);
    Optional<InventoryItem> findByItemCode(String itemCode);
    List<InventoryItem> findByCompanyIdAndQuantityLessThan(Long companyId, Double reorderLevel);
    List<InventoryItem> findByCompanyIdAndActiveTrue(Long companyId);
}
