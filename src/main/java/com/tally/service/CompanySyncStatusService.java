package com.tally.service;

import com.tally.entity.CompanySyncStatus;
import com.tally.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CompanySyncStatusService {
    
    @Autowired
    private CompanySyncStatusRepository repository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private LedgerRepository ledgerRepository;
    
    @Autowired
    private StockItemRepository stockItemRepository;
    
    @Autowired
    private CostCategoryRepository costCategoryRepository;
    
    @Autowired
    private CostCenterRepository costCenterRepository;
    
    @Autowired
    private CurrencyRepository currencyRepository;
    
    @Autowired
    private GodownRepository godownRepository;
    
    @Autowired
    private StockCategoryRepository stockCategoryRepository;
    
    @Autowired
    private StockGroupRepository stockGroupRepository;
    
    @Autowired
    private TaxUnitRepository taxUnitRepository;
    
    @Autowired
    private UnitsRepository unitsRepository;
    
    @Autowired
    private VoucherTypeRepository voucherTypeRepository;
    
    public Long getLastAlterId(Long cmpId) {
        Optional<CompanySyncStatus> status = repository.findByCmpId(cmpId);
        return status.map(CompanySyncStatus::getLastAlterId).orElse(0L);
    }
    
    public Long getCurrentMaxAlterId(Long cmpId) {
        Long maxAlterId = repository.findMaxAlterIdForCompany(cmpId);
        return maxAlterId != null ? maxAlterId : 0L;
    }
    
    public void updateLastAlterId(Long cmpId, Long alterId, String entityType) {
        Optional<CompanySyncStatus> existing = repository.findByCmpId(cmpId);
        
        CompanySyncStatus status;
        if (existing.isPresent()) {
            status = existing.get();
            status.setLastAlterId(alterId);
            status.setEntityType(entityType);
            status.setLastSyncTime(LocalDateTime.now());
        } else {
            status = new CompanySyncStatus();
            status.setCmpId(cmpId);
            status.setLastAlterId(alterId);
            status.setEntityType(entityType);
            status.setLastSyncTime(LocalDateTime.now());
        }
        
        repository.save(status);
    }
    
    public Map<String, Long> getEntityAlterIdMapping(Long cmpId) {
        Map<String, Long> mapping = new HashMap<>();
        
        // Get max AlterID for each entity
        mapping.put("group", groupRepository.getMaxAlterIdForCompany(cmpId));
        mapping.put("ledger", ledgerRepository.getMaxAlterIdForCompany(cmpId));
        mapping.put("stockitem", stockItemRepository.getMaxAlterIdForCompany(cmpId));
        mapping.put("costcategory", costCategoryRepository.getMaxAlterIdForCompany(cmpId));
        mapping.put("costcenter", costCenterRepository.getMaxAlterIdForCompany(cmpId));
        mapping.put("currency", currencyRepository.getMaxAlterIdForCompany(cmpId));
        mapping.put("godown", godownRepository.getMaxAlterIdForCompany(cmpId));
        mapping.put("stockcategory", stockCategoryRepository.getMaxAlterIdForCompany(cmpId));
        mapping.put("stockgroup", stockGroupRepository.getMaxAlterIdForCompany(cmpId));
        mapping.put("taxunit", taxUnitRepository.getMaxAlterIdForCompany(cmpId));
        mapping.put("units", unitsRepository.getMaxAlterIdForCompany(cmpId));
        mapping.put("vouchertype", voucherTypeRepository.getMaxAlterIdForCompany(cmpId));
        
        return mapping;
    }
}