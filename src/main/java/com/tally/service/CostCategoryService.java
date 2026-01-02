package com.tally.service;

import com.tally.entity.CostCategory;
import com.tally.repository.CostCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CostCategoryService {

    @Autowired
    private CostCategoryRepository costCategoryRepository;

    public List<CostCategory> getAll() {
        return costCategoryRepository.findAll();
    }

    public CostCategory create(CostCategory costCategory) {
        return costCategoryRepository.save(costCategory);
    }

    public CostCategory upsert(CostCategory costCategory) {
        // Use reconciliation identifier: cmpId + masterId
        Optional<CostCategory> existing = costCategoryRepository.findByCmpIdAndMasterId(
                costCategory.getCmpId(),
                costCategory.getMasterId());

        if (existing.isPresent()) {
            CostCategory e = existing.get();
            e.setGuid(costCategory.getGuid());
            e.setMasterId(costCategory.getMasterId());
            e.setAlterId(costCategory.getAlterId());
            e.setAllocateRevenue(costCategory.getAllocateRevenue());
            e.setAllocateNonRevenue(costCategory.getAllocateNonRevenue());
            e.setIsActive(costCategory.getIsActive());
            e.setUserId(costCategory.getUserId());
            e.setUpdatedAt(LocalDateTime.now());
            return costCategoryRepository.save(e);
        } else {
            return costCategoryRepository.save(costCategory);
        }
    }

    public void syncFromTally(List<CostCategory> items) {
        for (CostCategory item : items) {
            upsert(item);
        }
    }

    public List<CostCategory> saveAll(List<CostCategory> items) {
        return costCategoryRepository.saveAll(items);
    }
}
