package com.tally.repository;

import com.tally.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);
    Optional<Company> findByCompanyGuid(String companyGuid);
    List<Company> findByState(String state);
    List<Company> findByCountry(String country);
    List<Company> findByUserId(Long userId);
}
