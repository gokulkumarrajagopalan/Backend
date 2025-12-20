package com.tally.controller;

import com.tally.entity.CostCategory;
import com.tally.service.CostCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cost-categories")
@CrossOrigin(origins = "*")
public class CostCategoryController {

    @Autowired
    private CostCategoryService costCategoryService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        try {
            List<CostCategory> list = costCategoryService.getAll();
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

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> sync(@RequestBody List<CostCategory> items) {
        try {
            costCategoryService.syncFromTally(items);
            System.out.println("Cost Categories synced: " + items.size());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cost Categories synced successfully");
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
