package com.tally.service;

import com.tally.entity.StockCategory;
import com.tally.repository.StockCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockCategoryService {

    @Autowired
    private StockCategoryRepository stockCategoryRepository;

    public List<StockCategory> getAll() {
        return stockCategoryRepository.findAll();
    }

    public List<StockCategory> getByCmpId(Long cmpId) {
        return stockCategoryRepository.findByCmpId(cmpId);
    }

    public StockCategory create(StockCategory stockCategory) {
        return stockCategoryRepository.save(stockCategory);
    }

    public StockCategory upsert(StockCategory stockCategory) {
        // Use reconciliation identifier: cmpId + masterId
        Optional<StockCategory> existing = stockCategoryRepository.findByCmpIdAndMasterId(
                stockCategory.getCmpId(),
                stockCategory.getMasterId());

        if (existing.isPresent()) {
            StockCategory e = existing.get();
            e.setGuid(stockCategory.getGuid());
            e.setMasterId(stockCategory.getMasterId());
            e.setAlterId(stockCategory.getAlterId());
            e.setParent(stockCategory.getParent());
            e.setReservedName(stockCategory.getReservedName());
            e.setUserId(stockCategory.getUserId());
            e.setUpdatedAt(LocalDateTime.now());
            return stockCategoryRepository.save(e);
        } else {
            return stockCategoryRepository.save(stockCategory);
        }
    }

    public void syncFromTally(List<StockCategory> items) {
        for (StockCategory item : items) {
            upsert(item);
        }
    }

    public List<StockCategory> saveAll(List<StockCategory> items) {
        return stockCategoryRepository.saveAll(items);
    }
}
