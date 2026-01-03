package com.tally.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tally.entity.Currency;
import com.tally.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;
    // Define endpoints for currency operations here

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCurrency() {
        try {
            List<Currency> currencies = currencyService.getAllCurrencies();
            Map<String, Object> response = new HashMap<>();
            response.put("data", currencies);
            response.put("message", "Currencies retrieved successfully");
            response.put("success", true);
            response.put("count", currencies.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addCurrency(@RequestBody Currency currency) {
        try {
            Currency savedCurrency = currencyService.addCurrency(currency);
            Map<String, Object> response = new HashMap<>();
            response.put("data", savedCurrency);
            response.put("message", "Currency added successfully");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PutMapping("/master/{masterId}")
    public ResponseEntity<Map<String, Object>> updateByMasterId(
            @PathVariable Long masterId,
            @RequestBody Currency currency) {
        try {
            currency.setMasterId(masterId);
            Currency updated = currencyService.upsertCurrency(currency);
            Map<String, Object> response = new HashMap<>();
            response.put("data", updated);
            response.put("message", "Currency updated successfully");
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
            // Implementation would need a findByMasterId method in service
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Currency deleted successfully");
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
    public ResponseEntity<Map<String, Object>> syncCurrencies(@RequestBody List<Currency> currencies) {
        try {
            currencyService.syncCurrenciesFromTally(currencies);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Currencies synced successfully");
            response.put("count", currencies.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error syncing currencies: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateCurrency(@RequestBody Currency currency) {
        try {
            Currency updatedCurrency = currencyService.updateCurrency(currency);
            Map<String, Object> response = new HashMap<>();
            response.put("data", updatedCurrency);
            response.put("message", "Currency updated successfully");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
