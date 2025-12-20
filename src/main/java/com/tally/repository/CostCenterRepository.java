package com.tally.repository;

import com.tally.entity.CostCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CostCenterRepository extends JpaRepository<CostCenter, Long> {
    Optional<CostCenter> findByCmpIdAndName(Long cmpId, String name);
}
