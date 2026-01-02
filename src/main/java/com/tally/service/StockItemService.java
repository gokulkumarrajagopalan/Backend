package com.tally.service;

import com.tally.entity.StockItem;
import com.tally.repository.StockItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockItemService {

    @Autowired
    private StockItemRepository stockItemRepository;

    public List<StockItem> getAll() {
        return stockItemRepository.findAll();
    }

    public List<StockItem> getByCmpId(Long cmpId) {
        return stockItemRepository.findByCmpId(cmpId);
    }
    

    public StockItem create(StockItem stockItem) {
        return stockItemRepository.save(stockItem);
    }

    public StockItem upsert(StockItem stockItem) {
        // Use reconciliation identifier: cmpId + masterId
        Optional<StockItem> existing = stockItemRepository.findByCmpIdAndMasterId(
                stockItem.getCmpId(),
                stockItem.getMasterId());

        if (existing.isPresent()) {
            StockItem e = existing.get();
            e.setGuid(stockItem.getGuid());
            e.setMasterId(stockItem.getMasterId());
            e.setAlterId(stockItem.getAlterId());
            e.setParent(stockItem.getParent());
            e.setCategory(stockItem.getCategory());
            e.setDescription(stockItem.getDescription());
            e.setMailingName(stockItem.getMailingName());
            e.setReservedName(stockItem.getReservedName());
            e.setBaseUnits(stockItem.getBaseUnits());
            e.setAdditionalUnits(stockItem.getAdditionalUnits());
            e.setOpeningBalance(stockItem.getOpeningBalance());
            e.setOpeningValue(stockItem.getOpeningValue());
            e.setOpeningRate(stockItem.getOpeningRate());
            e.setCostingMethod(stockItem.getCostingMethod());
            e.setValuationMethod(stockItem.getValuationMethod());
            e.setGstTypeOfSupply(stockItem.getGstTypeOfSupply());
            e.setHsnCode(stockItem.getHsnCode());
            e.setBatchWiseOn(stockItem.getBatchWiseOn());
            e.setCostCentersOn(stockItem.getCostCentersOn());
            e.setUserId(stockItem.getUserId());
            e.setUpdatedAt(LocalDateTime.now());
            return stockItemRepository.save(e);
        } else {
            return stockItemRepository.save(stockItem);
        }
    }

    public void syncFromTally(List<StockItem> items) {
        for (StockItem item : items) {
            upsert(item);
        }
    }

    public List<StockItem> saveAll(List<StockItem> items) {
        return stockItemRepository.saveAll(items);
    }
}
