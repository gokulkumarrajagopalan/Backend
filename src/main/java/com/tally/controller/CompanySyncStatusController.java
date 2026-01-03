package com.tally.controller;

import com.tally.service.CompanySyncStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
public class CompanySyncStatusController {
    
    @Autowired
    private CompanySyncStatusService syncStatusService;
    
    @GetMapping("/{cmpId}/last-alter-id")
    public ResponseEntity<Map<String, Object>> getLastAlterId(@PathVariable Long cmpId) {
        try {
            Long lastAlterId = syncStatusService.getLastAlterId(cmpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("lastAlterID", lastAlterId);
            response.put("cmpId", cmpId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/{cmpId}/max-alter-id")
    public ResponseEntity<Map<String, Object>> getCurrentMaxAlterId(@PathVariable Long cmpId) {
        try {
            Long maxAlterId = syncStatusService.getCurrentMaxAlterId(cmpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("maxAlterID", maxAlterId);
            response.put("cmpId", cmpId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/{cmpId}/last-alter-id")
    public ResponseEntity<Map<String, Object>> updateLastAlterId(
            @PathVariable Long cmpId,
            @RequestBody Map<String, Object> payload) {
        try {
            Long alterId = Long.valueOf(payload.get("lastAlterID").toString());
            String entityType = (String) payload.get("entityType");
            
            syncStatusService.updateLastAlterId(cmpId, alterId, entityType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Last AlterID updated successfully");
            response.put("lastAlterID", alterId);
            response.put("cmpId", cmpId);
            response.put("lastSyncTime", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/{cmpId}/master-mapping")
    public ResponseEntity<Map<String, Object>> getMasterAlterIdMapping(@PathVariable Long cmpId) {
        try {
            Map<String, Long> masterMapping = syncStatusService.getEntityAlterIdMapping(cmpId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cmpId", cmpId);
            response.put("masters", masterMapping);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    } }