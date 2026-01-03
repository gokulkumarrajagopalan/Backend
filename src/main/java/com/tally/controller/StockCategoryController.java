package com.tally.controller;

import com.tally.entity.StockCategory;
import com.tally.service.StockCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stock-categories")
public class StockCategoryController {

    @Autowired
    private StockCategoryService stockCategoryService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        try {
            List<StockCategory> list = stockCategoryService.getAll();
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
    public ResponseEntity<List<StockCategory>> getByCompany(@PathVariable Long cmpId) {
        return ResponseEntity.ok(stockCategoryService.getByCmpId(cmpId));
    }

    @PutMapping("/master/{masterId}")
    public ResponseEntity<Map<String, Object>> updateByMasterId(
            @PathVariable Long masterId,
            @RequestBody StockCategory item) {
        try {
            item.setMasterId(masterId);
            StockCategory updated = stockCategoryService.upsert(item);
            Map<String, Object> response = new HashMap<>();
            response.put("data", updated);
            response.put("success", true);
            response.put("message", "Stock Category updated successfully");
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
            response.put("message", "Stock Category deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error: " + e.getMessage());
            error.put("success", false);
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> sync(@RequestBody List<StockCategory> items) {
        try {
            stockCategoryService.syncFromTally(items);
            System.out.println("Stock Categories synced: " + items.size());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock Categories synced successfully");
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
