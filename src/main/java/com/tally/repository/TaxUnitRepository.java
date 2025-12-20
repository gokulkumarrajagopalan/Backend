package com.tally.repository;

import com.tally.entity.TaxUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TaxUnitRepository extends JpaRepository<TaxUnit, Long> {
    Optional<TaxUnit> findByCmpIdAndName(Long cmpId, String name);
}
