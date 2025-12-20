package com.tally.controller;

import com.tally.entity.Company;
import com.tally.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCompany(@RequestBody Company company) {
        try {
            // System.out.println("\n========== COMPANY CREATION REQUEST ==========");
            // System.out.println("Company Name: " + company.getName());
            // System.out.println("User ID: " + company.getUserId());
            // System.out.println("Financial Year Start: " + company.getFinancialYearStart());
            // System.out.println("Books Start: " + company.getBooksStart());
            // System.out.println("GST State: " + company.getGstState());
            // System.out.println("GSTIN: " + company.getGstin());
            // System.out.println("Currency: " + company.getCurrencyFormalName());
            // System.out.println("===========================================\n");
            
            if (company.getUserId() == null) {
                throw new RuntimeException("User ID is required");
            }
            
            Company createdCompany = companyService.createCompany(company);
            
            // System.out.println("✓ Company Created Successfully!");
            // System.out.println("Company ID: " + createdCompany.getId());
            // System.out.println("=========================================\n");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Company created successfully");
            response.put("data", createdCompany);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCompanies() {
        try {
            List<Company> companies = companyService.getAllCompanies();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", companies);
            response.put("count", companies.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCompanyById(@PathVariable Long id) {
        try {
            Optional<Company> company = companyService.getCompanyById(id);
            Map<String, Object> response = new HashMap<>();
            if (company.isPresent()) {
                response.put("success", true);
                response.put("data", company.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Company not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCompany(@PathVariable Long id, @RequestBody Company company) {
        try {
            Company updatedCompany = companyService.updateCompany(id, company);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Company updated successfully");
            response.put("data", updatedCompany);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCompany(@PathVariable Long id) {
        try {
            companyService.deleteCompany(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Company deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getCompanyByName(@PathVariable String name) {
        try {
            Optional<Company> company = companyService.findByName(name);
            Map<String, Object> response = new HashMap<>();
            if (company.isPresent()) {
                response.put("success", true);
                response.put("data", company.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Company not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<Map<String, Object>> getCompaniesByCountry(@PathVariable String country) {
        try {
            List<Company> companies = companyService.findByCountry(country);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", companies);
            response.put("count", companies.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getCompaniesByUserId(@PathVariable Long userId) {
        try {
            List<Company> companies = companyService.findByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", companies);
            response.put("count", companies.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
