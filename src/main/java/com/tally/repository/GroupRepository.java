package com.tally.repository;

import com.tally.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    // Find by company
    List<Group> findByCmpId(Long cmpId);
    
    // Find by user
    List<Group> findByUserId(Long userId);
    
    // Find by company and name
    Optional<Group> findByCmpIdAndGrpName(Long cmpId, String grpName);
    
    // Find by company and active status
    List<Group> findByCmpIdAndIsActiveAndIsDeleted(Long cmpId, Boolean isActive, Boolean isDeleted);
    
    // Find by Tally GUID
    Optional<Group> findByGuid(String guid);
    
    // Find by Tally Master ID
    Optional<Group> findByMasterId(Long masterId);
    
    // Find by parent group
    List<Group> findByGrpParent(String grpParent);
    
    // Find by parent group ID
    List<Group> findByParentGrpId(Long parentGrpId);
    
    // Find primary groups (no parent)
    @Query("SELECT g FROM Group g WHERE g.parentGrpId IS NULL AND g.cmpId = :cmpId AND g.isDeleted = false")
    List<Group> findPrimaryGroups(@Param("cmpId") Long cmpId);
    
    // Find revenue groups (P&L)
    List<Group> findByCmpIdAndIsRevenue(Long cmpId, Boolean isRevenue);
    
    // Find by nature
    List<Group> findByCmpIdAndGrpNature(Long cmpId, String grpNature);
    
    // Search by name
    @Query("SELECT g FROM Group g WHERE g.cmpId = :cmpId AND LOWER(g.grpName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND g.isDeleted = false")
    List<Group> searchByName(@Param("cmpId") Long cmpId, @Param("searchTerm") String searchTerm);
    
    // Get group hierarchy
    @Query(value = "WITH RECURSIVE group_tree AS ( " +
           "SELECT grpid, grp_name, grp_parent, parent_grpid, 0 as level " +
           "FROM groups WHERE parent_grpid IS NULL AND cmpid = :cmpId AND is_deleted = false " +
           "UNION ALL " +
           "SELECT g.grpid, g.grp_name, g.grp_parent, g.parent_grpid, gt.level + 1 " +
           "FROM groups g INNER JOIN group_tree gt ON g.parent_grpid = gt.grpid " +
           "WHERE g.is_deleted = false " +
           ") SELECT * FROM group_tree ORDER BY level, grp_name", nativeQuery = true)
    List<Object[]> getGroupHierarchy(@Param("cmpId") Long cmpId);
}
