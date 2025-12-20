package com.tally.repository;

import com.tally.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
	Optional<Currency> findByCmpIdAndName(Long cmpId, String name);
}
