package com.tally.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tally.entity.Units;
import com.tally.service.UnitsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/units")
public class UnitsController {

    @Autowired
    private UnitsService unitsService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUnits() {
        try {
            List<Units> units = unitsService.getallUnits();
            Map<String, Object> response = new HashMap<>();
            response.put("data", units);
            response.put("message", "Units are retrived Sucessfully");
            response.put("sucess", true);
            response.put("count", units.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error" + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/company/{cmpId}")
    public ResponseEntity<List<Units>> getByCompany(@PathVariable Long cmpId) {
        return ResponseEntity.ok(unitsService.getByCmpId(cmpId));
    }

    @PutMapping("/master/{masterId}")
    public ResponseEntity<Map<String, Object>> updateByMasterId(
            @PathVariable Long masterId,
            @RequestBody Units unit) {
        try {
            unit.setMasterId(masterId);
            Units updated = unitsService.upsertUnits(unit);
            Map<String, Object> response = new HashMap<>();
            response.put("data", updated);
            response.put("message", "Unit updated successfully");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @DeleteMapping("/master/{masterId}")
    public ResponseEntity<Map<String, Object>> deleteByMasterId(@PathVariable Long masterId) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Unit deleted successfully");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncUnits(@RequestBody List<Units> units) {
        try {
            unitsService.syncUnitFromTally(units);
            HashMap<String, Object> response = new HashMap<>();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
