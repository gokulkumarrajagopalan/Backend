package com.tally.service;

import com.tally.entity.CostCenter;
import com.tally.repository.CostCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CostCenterService {

    @Autowired
    private CostCenterRepository costCenterRepository;

    public List<CostCenter> getAll() {
        return costCenterRepository.findAll();
    }

    public CostCenter create(CostCenter costCenter) {
        return costCenterRepository.save(costCenter);
    }

    public CostCenter upsert(CostCenter costCenter) {
        // Use reconciliation identifier: cmpId + masterId
        Optional<CostCenter> existing = costCenterRepository.findByCmpIdAndMasterId(
                costCenter.getCmpId(),
                costCenter.getMasterId());

        if (existing.isPresent()) {
            CostCenter e = existing.get();
            e.setGuid(costCenter.getGuid());
            e.setMasterId(costCenter.getMasterId());
            e.setAlterId(costCenter.getAlterId());
            e.setParent(costCenter.getParent());
            e.setCategory(costCenter.getCategory());
            e.setIsActive(costCenter.getIsActive());
            e.setUserId(costCenter.getUserId());
            e.setUpdatedAt(LocalDateTime.now());
            return costCenterRepository.save(e);
        } else {
            return costCenterRepository.save(costCenter);
        }
    }

    public void syncFromTally(List<CostCenter> items) {
        for (CostCenter item : items) {
            upsert(item);
        }
    }
}
