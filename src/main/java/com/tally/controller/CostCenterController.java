package com.tally.controller;

import com.tally.entity.CostCenter;
import com.tally.service.CostCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cost-centers")
@CrossOrigin(origins = "*")
public class CostCenterController {

    @Autowired
    private CostCenterService costCenterService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        try {
            List<CostCenter> list = costCenterService.getAll();
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
    public ResponseEntity<Map<String, Object>> sync(@RequestBody List<CostCenter> items) {
        try {
            costCenterService.syncFromTally(items);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cost Centers synced successfully");
            response.put("count", items.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("success", false);
            return ResponseEntity.status(500).body(error);
        }
    }
}
