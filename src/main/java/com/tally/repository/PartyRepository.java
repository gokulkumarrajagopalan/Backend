package com.tally.repository;

import com.tally.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {
    List<Party> findByCompanyId(Long companyId);
    Optional<Party> findByCompanyIdAndName(Long companyId, String name);
    List<Party> findByCompanyIdAndPartyType(Long companyId, String partyType);
    List<Party> findByCompanyIdAndActiveTrue(Long companyId);
    List<Party> findByCompanyIdAndCity(Long companyId, String city);
}
