package com.tally.service;

import com.tally.entity.Units;
import com.tally.repository.UnitsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnitsService {

    @Autowired
    private UnitsRepository unitsRepository;

    public List<Units> getallUnits() {
        return unitsRepository.findAll();
    }

    public List<Units> getByCmpId(Long cmpId) {
        return unitsRepository.findByCmpId(cmpId);
    }

    @Transactional
    public void syncUnitFromTally(List<Units> units) {
        for (Units unit : units) {
            upsertUnits(unit);
        }
    }

    public Units upsertUnits(Units unit) {
        // Use reconciliation identifier: cmpId + masterId
        Optional<Units> existingUnits = unitsRepository.findByCmpIdAndMasterId(
                unit.getCmpId(),
                unit.getMasterId());

        if (existingUnits.isPresent()) {
            Units existing = existingUnits.get();

            existing.setUnitName(unit.getUnitName());
            existing.setAlterId(unit.getAlterId());
            existing.setMasterId(unit.getMasterId());
            existing.setSimpleUnit(unit.isSimpleUnit());

            return unitsRepository.save(existing);

        } else {
            return unitsRepository.save(unit);
        }
    }
}
