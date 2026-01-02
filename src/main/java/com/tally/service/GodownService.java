package com.tally.service;

import com.tally.entity.Godown;
import com.tally.repository.GodownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GodownService {

    @Autowired
    private GodownRepository godownRepository;

    public List<Godown> getAll() {
        return godownRepository.findAll();
    }

    public List<Godown> getByCmpId(Long cmpId) {
        return godownRepository.findByCmpId(cmpId);
    }

    public Godown create(Godown godown) {
        return godownRepository.save(godown);
    }

    public Godown upsert(Godown godown) {
        // Use reconciliation identifier: cmpId + masterId
        Optional<Godown> existing = godownRepository.findByCmpIdAndMasterId(
                godown.getCmpId(),
                godown.getMasterId());

        if (existing.isPresent()) {
            Godown e = existing.get();
            e.setGuid(godown.getGuid());
            e.setMasterId(godown.getMasterId());
            e.setAlterId(godown.getAlterId());
            e.setAddress(godown.getAddress());
            e.setReservedName(godown.getReservedName());
            e.setUserId(godown.getUserId());
            e.setUpdatedAt(LocalDateTime.now());
            return godownRepository.save(e);
        } else {
            return godownRepository.save(godown);
        }
    }

    public void syncFromTally(List<Godown> items) {
        for (Godown item : items) {
            upsert(item);
        }
    }

    public List<Godown> saveAll(List<Godown> items) {
        return godownRepository.saveAll(items);
    }
}
