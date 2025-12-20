package com.tally.repository;

import com.tally.entity.CostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CostCategoryRepository extends JpaRepository<CostCategory, Long> {
    Optional<CostCategory> findByCmpIdAndName(Long cmpId, String name);
}
