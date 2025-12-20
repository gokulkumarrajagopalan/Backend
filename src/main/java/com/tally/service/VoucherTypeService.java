package com.tally.service;

import com.tally.entity.VoucherType;
import com.tally.repository.VoucherTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VoucherTypeService {

    @Autowired
    private VoucherTypeRepository voucherTypeRepository;

    public List<VoucherType> getAll() {
        return voucherTypeRepository.findAll();
    }

    public VoucherType create(VoucherType voucherType) {
        return voucherTypeRepository.save(voucherType);
    }

    public VoucherType upsert(VoucherType voucherType) {
        Optional<VoucherType> existing = voucherTypeRepository.findByCmpIdAndName(
                voucherType.getCmpId(),
                voucherType.getName());

        if (existing.isPresent()) {
            VoucherType e = existing.get();
            e.setGuid(voucherType.getGuid());
            e.setMasterId(voucherType.getMasterId());
            e.setAlterId(voucherType.getAlterId());
            e.setParent(voucherType.getParent());
            e.setNumberingMethod(voucherType.getNumberingMethod());
            e.setIsActive(voucherType.getIsActive());
            e.setUserId(voucherType.getUserId());
            e.setUpdatedAt(LocalDateTime.now());
            return voucherTypeRepository.save(e);
        } else {
            return voucherTypeRepository.save(voucherType);
        }
    }

    public void syncFromTally(List<VoucherType> items) {
        for (VoucherType item : items) {
            upsert(item);
        }
    }
}
