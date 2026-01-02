package com.tally.controller;

import com.tally.entity.StockItem;
import com.tally.service.StockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stock-items")
@CrossOrigin(origins = "*")
public class StockItemController {

    @Autowired
    private StockItemService stockItemService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        try {
            List<StockItem> list = stockItemService.getAll();
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
    public ResponseEntity<List<StockItem>> getByCompany(@PathVariable Long cmpId) {
        return ResponseEntity.ok(stockItemService.getByCmpId(cmpId));
    }

    @PutMapping("/master/{masterId}")
    public ResponseEntity<Map<String, Object>> updateByMasterId(
            @PathVariable Long masterId,
            @RequestBody StockItem item) {
        try {
            item.setMasterId(masterId);
            StockItem updated = stockItemService.upsert(item);
            Map<String, Object> response = new HashMap<>();
            response.put("data", updated);
            response.put("success", true);
            response.put("message", "Stock Item updated successfully");
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
            response.put("message", "Stock Item deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error: " + e.getMessage());
            error.put("success", false);
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> sync(@RequestBody List<StockItem> items) {
        try {
            stockItemService.syncFromTally(items);
            System.out.println("Stock Items synced: " + items.size());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock Items synced successfully");
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
