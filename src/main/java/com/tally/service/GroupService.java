package com.tally.service;

import com.tally.entity.Group;
import com.tally.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupService {
    
    @Autowired
    private GroupRepository groupRepository;
    
    /**
     * Get all groups (all companies)
     */
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }
    
    /**
     * Get all groups for a specific company
     */
    public List<Group> getGroupsByCompany(Long cmpId) {
        return groupRepository.findByCmpId(cmpId);
    }
    
    /**
     * Get active groups for a company
     */
    public List<Group> getActiveGroups(Long cmpId) {
        return groupRepository.findByCmpIdAndIsActiveAndIsDeleted(cmpId, true, false);
    }
    
    /**
     * Get group by ID
     */
    public Optional<Group> getGroupById(Long grpId) {
        return groupRepository.findById(grpId);
    }
    
    /**
     * Get group by GUID (Tally identifier)
     */
    public Optional<Group> getGroupByGuid(String guid) {
        return groupRepository.findByGuid(guid);
    }
    
    /**
     * Get group by name in a company
     */
    public Optional<Group> getGroupByName(Long cmpId, String grpName) {
        return groupRepository.findByCmpIdAndGrpName(cmpId, grpName);
    }
    
    /**
     * Create new group
     */
    public Group createGroup(Group group) {
        if (group.getCreatedAt() == null) {
            group.setCreatedAt(LocalDateTime.now());
        }
        group.setUpdatedAt(LocalDateTime.now());
        return groupRepository.save(group);
    }
    
    /**
     * Create or update group - Upsert based on (cmpId, grpName)
     * This ensures each company can have their own "Cash", "Bank", etc. groups
     * 
     * CRITICAL FOR MULTI-COMPANY SUPPORT:
     * - Uses (cmpId, grpName) as unique identifier, NOT GUID alone
     * - Company 1 "Cash" and Company 2 "Cash" are SEPARATE records
     * - GUID is company-specific: CMP1-xxx, CMP2-xxx
     */
    public Group upsertGroup(Group group) {
        // Find existing by company + name (CRITICAL FOR MULTI-COMPANY!)
        Optional<Group> existingGroup = groupRepository.findByCmpIdAndGrpName(
            group.getCmpId(), 
            group.getGrpName()
        );
        
        if (existingGroup.isPresent()) {
            // UPDATE existing group for this company
            Group existing = existingGroup.get();
            
            System.out.println("   ♻️  Updating existing group: " + existing.getGrpName() 
                + " for Company #" + existing.getCmpId());
            
            // Update all fields except primary key
            existing.setGuid(group.getGuid());              // Update company-specific GUID
            existing.setMasterId(group.getMasterId());      // Update Tally Master ID
            existing.setAlterId(group.getAlterId());        // Update Tally Alter ID
            existing.setGrpCode(group.getGrpCode());
            existing.setGrpAlias(group.getGrpAlias());
            existing.setGrpParent(group.getGrpParent());
            existing.setGrpPrimaryGroup(group.getGrpPrimaryGroup());
            existing.setGrpNature(group.getGrpNature());
            existing.setIsRevenue(group.getIsRevenue());
            existing.setIsReserved(group.getIsReserved());
            existing.setReservedName(group.getReservedName());
            existing.setAlternateNames(group.getAlternateNames());
            existing.setLanguageId(group.getLanguageId());
            existing.setParentGrpId(group.getParentGrpId());
            existing.setLevelNumber(group.getLevelNumber());
            existing.setFullPath(group.getFullPath());
            existing.setIsActive(true);
            existing.setIsDeleted(false);
            existing.setSyncStatus("SYNCED");
            existing.setLastSyncDate(LocalDateTime.now());
            existing.setUpdatedAt(LocalDateTime.now());
            
            return groupRepository.save(existing);
        } else {
            // CREATE new group for this company
            System.out.println("   ➕ Creating new group: " + group.getGrpName() 
                + " for Company #" + group.getCmpId());
            
            // Ensure timestamps and defaults are set
            if (group.getCreatedAt() == null) {
                group.setCreatedAt(LocalDateTime.now());
            }
            group.setUpdatedAt(LocalDateTime.now());
            
            if (group.getSyncStatus() == null) {
                group.setSyncStatus("SYNCED");
            }
            
            if (group.getLastSyncDate() == null) {
                group.setLastSyncDate(LocalDateTime.now());
            }
            
            if (group.getIsActive() == null) {
                group.setIsActive(true);
            }
            
            if (group.getIsDeleted() == null) {
                group.setIsDeleted(false);
            }
            
            return groupRepository.save(group);
        }
    }
    
    /**
     * Update existing group
     */
    public Group updateGroup(Long grpId, Group group) {
        Optional<Group> existingGroup = groupRepository.findById(grpId);
        if (existingGroup.isPresent()) {
            group.setGrpId(grpId);
            group.setUpdatedAt(LocalDateTime.now());
            return groupRepository.save(group);
        }
        throw new RuntimeException("Group not found with ID: " + grpId);
    }
    
    /**
     * Soft delete group
     */
    public void deleteGroup(Long grpId) {
        Optional<Group> group = groupRepository.findById(grpId);
        if (group.isPresent()) {
            Group g = group.get();
            g.setIsDeleted(true);
            g.setIsActive(false);
            g.setUpdatedAt(LocalDateTime.now());
            groupRepository.save(g);
        }
    }
    
    /**
     * Hard delete group
     */
    public void hardDeleteGroup(Long grpId) {
        groupRepository.deleteById(grpId);
    }
    
    /**
     * Get primary groups (top-level groups with no parent)
     */
    public List<Group> getPrimaryGroups(Long cmpId) {
        return groupRepository.findPrimaryGroups(cmpId);
    }
    
    /**
     * Get child groups of a parent
     */
    public List<Group> getChildGroups(Long parentGrpId) {
        return groupRepository.findByParentGrpId(parentGrpId);
    }
    
    /**
     * Get revenue groups (P&L groups)
     */
    public List<Group> getRevenueGroups(Long cmpId) {
        return groupRepository.findByCmpIdAndIsRevenue(cmpId, true);
    }
    
    /**
     * Get balance sheet groups
     */
    public List<Group> getBalanceSheetGroups(Long cmpId) {
        return groupRepository.findByCmpIdAndIsRevenue(cmpId, false);
    }
    
    /**
     * Search groups by name
     */
    public List<Group> searchGroups(Long cmpId, String searchTerm) {
        return groupRepository.searchByName(cmpId, searchTerm);
    }
    
    /**
     * Get groups by nature
     */
    public List<Group> getGroupsByNature(Long cmpId, String nature) {
        return groupRepository.findByCmpIdAndGrpNature(cmpId, nature);
    }
    
    /**
     * Get group hierarchy (recursive tree)
     */
    public List<Object[]> getGroupHierarchy(Long cmpId) {
        return groupRepository.getGroupHierarchy(cmpId);
    }
    
    /**
     * Sync groups from Tally (bulk operation)
     */
    @Transactional
    public void syncGroupsFromTally(List<Group> tallyGroups) {
        for (Group group : tallyGroups) {
            upsertGroup(group);
        }
    }
}
