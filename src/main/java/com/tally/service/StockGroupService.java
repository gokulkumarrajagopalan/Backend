package com.tally.service;

import com.tally.entity.StockGroup;
import com.tally.repository.StockGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockGroupService {

    @Autowired
    private StockGroupRepository stockGroupRepository;

    public List<StockGroup> getAll() {
        return stockGroupRepository.findAll();
    }

    public List<StockGroup> getByCmpId(Long cmpId) {
        return stockGroupRepository.findByCmpId(cmpId);
    }

    public StockGroup create(StockGroup stockGroup) {
        return stockGroupRepository.save(stockGroup);
    }

    public StockGroup upsert(StockGroup stockGroup) {
        // Use reconciliation identifier: cmpId + masterId
        Optional<StockGroup> existing = stockGroupRepository.findByCmpIdAndMasterId(
                stockGroup.getCmpId(),
                stockGroup.getMasterId());

        if (existing.isPresent()) {
            StockGroup e = existing.get();
            e.setGuid(stockGroup.getGuid());
            e.setMasterId(stockGroup.getMasterId());
            e.setAlterId(stockGroup.getAlterId());
            e.setParent(stockGroup.getParent());
            e.setReservedName(stockGroup.getReservedName());
            e.setUserId(stockGroup.getUserId());
            e.setUpdatedAt(LocalDateTime.now());
            return stockGroupRepository.save(e);
        } else {
            return stockGroupRepository.save(stockGroup);
        }
    }

    public void syncFromTally(List<StockGroup> items) {
        for (StockGroup item : items) {
            upsert(item);
        }
    }

    public List<StockGroup> saveAll(List<StockGroup> items) {
        return stockGroupRepository.saveAll(items);
    }
}
