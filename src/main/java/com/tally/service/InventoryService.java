package com.tally.service;

import com.tally.entity.InventoryItem;
import com.tally.repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    public InventoryItem createItem(InventoryItem item) {
        item.setCurrentValue(item.getQuantity() * item.getUnitCost());
        item.setLastStockDate(LocalDate.now());
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return inventoryItemRepository.save(item);
    }

    public InventoryItem updateItem(Long id, InventoryItem item) {
        Optional<InventoryItem> existing = inventoryItemRepository.findById(id);
        if (existing.isPresent()) {
            InventoryItem inv = existing.get();
            inv.setItemName(item.getItemName());
            inv.setCategory(item.getCategory());
            inv.setUnit(item.getUnit());
            inv.setReorderLevel(item.getReorderLevel());
            inv.setReorderQuantity(item.getReorderQuantity());
            inv.setUnitCost(item.getUnitCost());
            inv.setUnitPrice(item.getUnitPrice());
            inv.setCurrentValue(inv.getQuantity() * item.getUnitCost());
            inv.setUpdatedAt(LocalDateTime.now());
            return inventoryItemRepository.save(inv);
        }
        throw new RuntimeException("Inventory item not found");
    }

    public Optional<InventoryItem> getItemById(Long id) {
        return inventoryItemRepository.findById(id);
    }

    public List<InventoryItem> getAllItems(Long companyId) {
        return inventoryItemRepository.findByCompanyIdAndActiveTrue(companyId);
    }

    public List<InventoryItem> getItemsByCategory(Long companyId, String category) {
        return inventoryItemRepository.findByCompanyIdAndCategory(companyId, category);
    }

    public List<InventoryItem> getLowStockItems(Long companyId) {
        return inventoryItemRepository.findByCompanyIdAndQuantityLessThan(companyId, 10.0);
    }

    public InventoryItem updateQuantity(Long id, Double quantity) {
        Optional<InventoryItem> item = inventoryItemRepository.findById(id);
        if (item.isPresent()) {
            InventoryItem inv = item.get();
            inv.setQuantity(inv.getQuantity() + quantity);
            inv.setCurrentValue(inv.getQuantity() * inv.getUnitCost());
            inv.setLastStockDate(LocalDate.now());
            inv.setUpdatedAt(LocalDateTime.now());
            return inventoryItemRepository.save(inv);
        }
        throw new RuntimeException("Inventory item not found");
    }

    public Double getInventoryValue(Long companyId) {
        return getAllItems(companyId).stream()
            .mapToDouble(InventoryItem::getCurrentValue)
            .sum();
    }

    public void deactivateItem(Long id) {
        Optional<InventoryItem> item = inventoryItemRepository.findById(id);
        if (item.isPresent()) {
            InventoryItem inv = item.get();
            inv.setActive(false);
            inv.setUpdatedAt(LocalDateTime.now());
            inventoryItemRepository.save(inv);
        }
    }
}
