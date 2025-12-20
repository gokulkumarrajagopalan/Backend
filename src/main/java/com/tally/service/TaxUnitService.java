package com.tally.service;

import com.tally.entity.TaxUnit;
import com.tally.repository.TaxUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaxUnitService {

    @Autowired
    private TaxUnitRepository taxUnitRepository;

    public List<TaxUnit> getAll() {
        return taxUnitRepository.findAll();
    }

    public TaxUnit create(TaxUnit taxUnit) {
        return taxUnitRepository.save(taxUnit);
    }

    public TaxUnit upsert(TaxUnit taxUnit) {
        Optional<TaxUnit> existing = taxUnitRepository.findByCmpIdAndName(
                taxUnit.getCmpId(),
                taxUnit.getName());

        if (existing.isPresent()) {
            TaxUnit e = existing.get();
            e.setGuid(taxUnit.getGuid());
            e.setMasterId(taxUnit.getMasterId());
            e.setAlterId(taxUnit.getAlterId());
            e.setIsActive(taxUnit.getIsActive());
            e.setUserId(taxUnit.getUserId());
            e.setUpdatedAt(LocalDateTime.now());
            return taxUnitRepository.save(e);
        } else {
            return taxUnitRepository.save(taxUnit);
        }
    }

    public void syncFromTally(List<TaxUnit> items) {
        for (TaxUnit item : items) {
            upsert(item);
        }
    }
}
