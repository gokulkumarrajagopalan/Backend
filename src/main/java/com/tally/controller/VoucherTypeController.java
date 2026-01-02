package com.tally.controller;

import com.tally.entity.VoucherType;
import com.tally.service.VoucherTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/voucher-types")
@CrossOrigin(origins = "*")
public class VoucherTypeController {

    @Autowired
    private VoucherTypeService voucherTypeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        try {
            List<VoucherType> list = voucherTypeService.getAll();
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
            @RequestBody VoucherType item) {
        try {
            item.setMasterId(masterId);
            VoucherType updated = voucherTypeService.upsert(item);
            Map<String, Object> response = new HashMap<>();
            response.put("data", updated);
            response.put("success", true);
            response.put("message", "Voucher Type updated successfully");
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
            response.put("message", "Voucher Type deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error: " + e.getMessage());
            error.put("success", false);
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> sync(@RequestBody List<VoucherType> items) {
        try {
            voucherTypeService.syncFromTally(items);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Voucher Types synced successfully");
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
