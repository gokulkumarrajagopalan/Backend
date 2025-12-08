package com.tally.service;

import com.tally.entity.Company;
import com.tally.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    public Company createCompany(Company company) {
        if (company.getName() == null || company.getName().isEmpty()) {
            throw new RuntimeException("Company name is required");
        }
        company.setCreatedAt(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());
        return companyRepository.save(company);
    }

    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company updateCompany(Long id, Company company) {
        Optional<Company> existing = companyRepository.findById(id);
        if (existing.isPresent()) {
            Company comp = existing.get();
            if (company.getName() != null) comp.setName(company.getName());
            if (company.getMailingName() != null) comp.setMailingName(company.getMailingName());
            if (company.getAddressLine1() != null) comp.setAddressLine1(company.getAddressLine1());
            if (company.getAddressLine2() != null) comp.setAddressLine2(company.getAddressLine2());
            if (company.getAddressLine3() != null) comp.setAddressLine3(company.getAddressLine3());
            if (company.getAddressLine4() != null) comp.setAddressLine4(company.getAddressLine4());
            if (company.getEmail() != null) comp.setEmail(company.getEmail());
            if (company.getTelephone() != null) comp.setTelephone(company.getTelephone());
            if (company.getMobile() != null) comp.setMobile(company.getMobile());
            if (company.getState() != null) comp.setState(company.getState());
            if (company.getCountry() != null) comp.setCountry(company.getCountry());
            if (company.getWebsite() != null) comp.setWebsite(company.getWebsite());
            if (company.getPanNumber() != null) comp.setPanNumber(company.getPanNumber());
            if (company.getFinancialYearStart() != null) comp.setFinancialYearStart(company.getFinancialYearStart());
            if (company.getBooksStart() != null) comp.setBooksStart(company.getBooksStart());
            if (company.getCurrencySymbol() != null) comp.setCurrencySymbol(company.getCurrencySymbol());
            if (company.getCurrencyFormalName() != null) comp.setCurrencyFormalName(company.getCurrencyFormalName());
            if (company.getCurrencyDecimalPlaces() != null) comp.setCurrencyDecimalPlaces(company.getCurrencyDecimalPlaces());
            if (company.getGstState() != null) comp.setGstState(company.getGstState());
            if (company.getGstin() != null) comp.setGstin(company.getGstin());
            if (company.getBillwiseEnabled() != null) comp.setBillwiseEnabled(company.getBillwiseEnabled());
            if (company.getCostcentreEnabled() != null) comp.setCostcentreEnabled(company.getCostcentreEnabled());
            if (company.getBatchEnabled() != null) comp.setBatchEnabled(company.getBatchEnabled());
            comp.setUpdatedAt(LocalDateTime.now());
            return companyRepository.save(comp);
        }
        throw new RuntimeException("Company not found");
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    public Optional<Company> findByName(String name) {
        return companyRepository.findByName(name);
    }

    public Optional<Company> findByCompanyGuid(String companyGuid) {
        return companyRepository.findByCompanyGuid(companyGuid);
    }

    public List<Company> findByCountry(String country) {
        return companyRepository.findByCountry(country);
    }

    public List<Company> findByUserId(Long userId) {
        return companyRepository.findByUserId(userId);
    }
}
