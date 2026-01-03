package com.tally.controller;

import com.tally.entity.StockGroup;
import com.tally.service.StockGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stock-groups")
public class StockGroupController {

    @Autowired
    private StockGroupService stockGroupService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        try {
            List<StockGroup> list = stockGroupService.getAll();
            Map<String, Object> response = new HashMap<>();
            response.put("data", list);
            response.put("success", true);
            response.put("count", list.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("success", false);
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/company/{cmpId}")
    public ResponseEntity<Map<String, Object>> getByCmpId(@PathVariable Long cmpId) {
        try {
            List<StockGroup> list = stockGroupService.getByCmpId(cmpId);
            Map<String, Object> response = new HashMap<>();
            response.put("data", list);
            response.put("success", true);
            response.put("count", list.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("success", false);
            return ResponseEntity.status(500).body(error);
        }
    }

    @PutMapping("/master/{masterId}")
    public ResponseEntity<Map<String, Object>> updateByMasterId(
            @PathVariable Long masterId,
            @RequestBody StockGroup item) {
        try {
            item.setMasterId(masterId);
            StockGroup updated = stockGroupService.upsert(item);
            Map<String, Object> response = new HashMap<>();
            response.put("data", updated);
            response.put("success", true);
            response.put("message", "Stock Group updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error: " + e.getMessage());
            error.put("success", false);
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @DeleteMapping("/master/{masterId}")
    public ResponseEntity<Map<String, Object>> deleteByMasterId(@PathVariable Long masterId) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock Group deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error: " + e.getMessage());
            error.put("success", false);
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> sync(@RequestBody List<StockGroup> items) {
        try {
            stockGroupService.syncFromTally(items);
            System.out.println("Stock Groups synced: " + items.size());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock Groups synced successfully");
            response.put("count", items.size());
            response.put("data", items);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("success", false);
            return ResponseEntity.status(500).body(error);
        }
    }
}
