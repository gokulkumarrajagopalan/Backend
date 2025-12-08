package com.tally.repository;

import com.tally.entity.AccountMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountMasterRepository extends JpaRepository<AccountMaster, Long> {
    List<AccountMaster> findByCompanyId(Long companyId);
    Optional<AccountMaster> findByCompanyIdAndAccountCode(Long companyId, String accountCode);
    List<AccountMaster> findByCompanyIdAndAccountType(Long companyId, String accountType);
    List<AccountMaster> findByCompanyIdAndActiveTrue(Long companyId);
    Optional<AccountMaster> findByCompanyIdAndAccountName(Long companyId, String accountName);
}
