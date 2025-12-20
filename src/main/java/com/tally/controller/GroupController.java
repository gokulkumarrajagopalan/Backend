package com.tally.controller;

import com.tally.entity.Group;
import com.tally.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
public class GroupController {
    
    @Autowired
    private GroupService groupService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllGroups() {
        try {
            List<Group> groups = groupService.getAllGroups();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Groups retrieved successfully");
            response.put("data", groups);
            response.put("count", groups.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GET /groups/company/{cmpId} - Get all groups for a company
     */
    @GetMapping("/company/{cmpId}")
    public ResponseEntity<Map<String, Object>> getGroupsByCompany(@PathVariable Long cmpId) {
        try {
            List<Group> groups = groupService.getGroupsByCompany(cmpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Groups retrieved successfully");
            response.put("data", groups);
            response.put("count", groups.size());
            response.put("companyId", cmpId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GET /groups/company/{cmpId}/active - Get active groups for a company
     */
    @GetMapping("/company/{cmpId}/active")
    public ResponseEntity<Map<String, Object>> getActiveGroups(@PathVariable Long cmpId) {
        try {
            List<Group> groups = groupService.getActiveGroups(cmpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Active groups retrieved successfully");
            response.put("data", groups);
            response.put("count", groups.size());
            response.put("companyId", cmpId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving active groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GET /groups/{grpId} - Get group by ID
     */
    @GetMapping("/{grpId}")
    public ResponseEntity<Map<String, Object>> getGroupById(@PathVariable Long grpId) {
        try {
            Optional<Group> group = groupService.getGroupById(grpId);
            Map<String, Object> response = new HashMap<>();
            
            if (group.isPresent()) {
                response.put("success", true);
                response.put("message", "Group found");
                response.put("data", group.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Group not found with ID: " + grpId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving group: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GET /groups/guid/{guid} - Get group by Tally GUID
     */
    @GetMapping("/guid/{guid}")
    public ResponseEntity<Map<String, Object>> getGroupByGuid(@PathVariable String guid) {
        try {
            Optional<Group> group = groupService.getGroupByGuid(guid);
            Map<String, Object> response = new HashMap<>();
            
            if (group.isPresent()) {
                response.put("success", true);
                response.put("message", "Group found");
                response.put("data", group.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Group not found with GUID: " + guid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving group: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GET /groups/company/{cmpId}/primary - Get primary groups (top-level)
     */
    @GetMapping("/company/{cmpId}/primary")
    public ResponseEntity<Map<String, Object>> getPrimaryGroups(@PathVariable Long cmpId) {
        try {
            List<Group> groups = groupService.getPrimaryGroups(cmpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Primary groups retrieved successfully");
            response.put("data", groups);
            response.put("count", groups.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving primary groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GET /groups/company/{cmpId}/revenue - Get revenue groups (P&L)
     */
    @GetMapping("/company/{cmpId}/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueGroups(@PathVariable Long cmpId) {
        try {
            List<Group> groups = groupService.getRevenueGroups(cmpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Revenue groups (P&L) retrieved successfully");
            response.put("data", groups);
            response.put("count", groups.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving revenue groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GET /groups/company/{cmpId}/balancesheet - Get balance sheet groups
     */
    @GetMapping("/company/{cmpId}/balancesheet")
    public ResponseEntity<Map<String, Object>> getBalanceSheetGroups(@PathVariable Long cmpId) {
        try {
            List<Group> groups = groupService.getBalanceSheetGroups(cmpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Balance sheet groups retrieved successfully");
            response.put("data", groups);
            response.put("count", groups.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving balance sheet groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GET /groups/company/{cmpId}/hierarchy - Get group hierarchy tree
     */
    @GetMapping("/company/{cmpId}/hierarchy")
    public ResponseEntity<Map<String, Object>> getGroupHierarchy(@PathVariable Long cmpId) {
        try {
            List<Object[]> hierarchy = groupService.getGroupHierarchy(cmpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Group hierarchy retrieved successfully");
            response.put("data", hierarchy);
            response.put("count", hierarchy.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving hierarchy: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GET /groups/company/{cmpId}/search?term={searchTerm} - Search groups by name
     */
    @GetMapping("/company/{cmpId}/search")
    public ResponseEntity<Map<String, Object>> searchGroups(
            @PathVariable Long cmpId,
            @RequestParam String term) {
        try {
            List<Group> groups = groupService.searchGroups(cmpId, term);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Search completed");
            response.put("data", groups);
            response.put("count", groups.size());
            response.put("searchTerm", term);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error searching groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GET /groups/{parentGrpId}/children - Get child groups of a parent
     */
    @GetMapping("/{parentGrpId}/children")
    public ResponseEntity<Map<String, Object>> getChildGroups(@PathVariable Long parentGrpId) {
        try {
            List<Group> groups = groupService.getChildGroups(parentGrpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Child groups retrieved successfully");
            response.put("data", groups);
            response.put("count", groups.size());
            response.put("parentGrpId", parentGrpId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving child groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * POST /groups - Create new group
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createGroup(@RequestBody Group group) {
        try {
            // System.out.println("\n========== GROUP CREATION REQUEST ==========");
            // System.out.println("Group Name: " + group.getGrpName());
            // System.out.println("Company ID: " + group.getCmpId());
            // System.out.println("User ID: " + group.getUserId());
            // System.out.println("Parent: " + group.getGrpParent());
            // System.out.println("Nature: " + group.getGrpNature());
            // System.out.println("Is Revenue: " + group.getIsRevenue());
            // System.out.println("===========================================\n");
            
            Group createdGroup = groupService.createGroup(group);
            
            // System.out.println("✓ Group Created Successfully!");
            // System.out.println("Group ID: " + createdGroup.getGrpId());
            // System.out.println("=========================================\n");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Group created successfully");
            response.put("data", createdGroup);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error creating group: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * POST /groups/sync - Sync groups from Tally (bulk upsert)
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncGroups(@RequestBody List<Group> groups) {
        try {
            // System.out.println("\n========== TALLY SYNC REQUEST ==========");
            // System.out.println("Groups to sync: " + groups.size());
            // System.out.println("=========================================\n");
            
            groupService.syncGroupsFromTally(groups);
            
            // System.out.println("✓ Sync Completed Successfully!");
            // System.out.println("========================================\n");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Groups synced successfully");
            response.put("count", groups.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error syncing groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * PUT /groups/{grpId} - Update existing group
     */
    @PutMapping("/{grpId}")
    public ResponseEntity<Map<String, Object>> updateGroup(
            @PathVariable Long grpId,
            @RequestBody Group group) {
        try {
            Group updatedGroup = groupService.updateGroup(grpId, group);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Group updated successfully");
            response.put("data", updatedGroup);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating group: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * DELETE /groups/{grpId} - Soft delete group
     */
    @DeleteMapping("/{grpId}")
    public ResponseEntity<Map<String, Object>> deleteGroup(@PathVariable Long grpId) {
        try {
            groupService.deleteGroup(grpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Group deleted successfully");
            response.put("groupId", grpId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error deleting group: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * DELETE /groups/{grpId}/hard - Hard delete group (permanent)
     */
    @DeleteMapping("/{grpId}/hard")
    public ResponseEntity<Map<String, Object>> hardDeleteGroup(@PathVariable Long grpId) {
        try {
            groupService.hardDeleteGroup(grpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Group permanently deleted");
            response.put("groupId", grpId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error deleting group: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
