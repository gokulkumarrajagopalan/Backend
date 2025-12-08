package com.tally.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(unique = true)
    private String itemCode;
    
    @Column(nullable = false)
    private String itemName;
    
    private String category;
    private String unit; // kg, piece, meter, liter, etc
    private Double quantity;
    private Double reorderLevel;
    private Double reorderQuantity;
    private Double unitCost;
    private Double unitPrice;
    private String valuation; // FIFO, LIFO, WEIGHTED_AVERAGE
    private Double currentValue;
    private String description;
    private Boolean active;
    private LocalDate lastStockDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public InventoryItem() {
        this.quantity = 0.0;
        this.unitCost = 0.0;
        this.unitPrice = 0.0;
        this.currentValue = 0.0;
        this.active = true;
        this.valuation = "FIFO";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Double reorderLevel) { this.reorderLevel = reorderLevel; }

    public Double getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(Double reorderQuantity) { this.reorderQuantity = reorderQuantity; }

    public Double getUnitCost() { return unitCost; }
    public void setUnitCost(Double unitCost) { this.unitCost = unitCost; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public String getValuation() { return valuation; }
    public void setValuation(String valuation) { this.valuation = valuation; }

    public Double getCurrentValue() { return currentValue; }
    public void setCurrentValue(Double currentValue) { this.currentValue = currentValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDate getLastStockDate() { return lastStockDate; }
    public void setLastStockDate(LocalDate lastStockDate) { this.lastStockDate = lastStockDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
