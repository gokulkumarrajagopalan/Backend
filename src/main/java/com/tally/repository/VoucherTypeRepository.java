package com.tally.repository;

import com.tally.entity.VoucherType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoucherTypeRepository extends JpaRepository<VoucherType, Long> {
    Optional<VoucherType> findByCmpIdAndName(Long cmpId, String name);
}
