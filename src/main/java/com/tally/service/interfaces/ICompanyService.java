package com.tally.service.interfaces;

import com.tally.entity.Company;
import java.util.List;
import java.util.Optional;

/**
 * Interface for Company Service
 * Follows Interface Segregation Principle (ISP)
 */
public interface ICompanyService {
    
    /**
     * Create new company
     */
    Company createCompany(Company company);
    
    /**
     * Get company by ID
     */
    Optional<Company> getCompanyById(Long id);
    
    /**
     * Get all companies for a user
     */
    List<Company> getUserCompanies(Long userId);
    
    /**
     * Update company
     */
    Company updateCompany(Long id, Company company);
    
    /**
     * Delete company
     */
    void deleteCompany(Long id);
    
    /**
     * Get all companies
     */
    List<Company> getAllCompanies();
}
