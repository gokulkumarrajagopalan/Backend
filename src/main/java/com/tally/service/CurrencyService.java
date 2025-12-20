package com.tally.service;

import com.tally.entity.Currency;
import com.tally.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public Currency addCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }

    public Currency updateCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }

    /**
     * Create or update currency - Upsert based on (cmpid, name)
     */
    public Currency upsertCurrency(Currency currency) {
        Optional<Currency> existingCurrency = currencyRepository.findByCmpIdAndName(
                currency.getCmpId(),
                currency.getName());

        if (existingCurrency.isPresent()) {
            Currency existing = existingCurrency.get();

            // Update fields
            existing.setGuid(currency.getGuid());
            existing.setMasterId(currency.getMasterId());
            existing.setAlterId(currency.getAlterId());
            existing.setSymbol(currency.getSymbol());
            existing.setFormalName(currency.getFormalName());
            existing.setDecimalPlaces(currency.getDecimalPlaces());
            existing.setDecimalSeparator(currency.getDecimalSeparator());
            existing.setShowAmountInWords(currency.getShowAmountInWords());
            existing.setSuffixSymbol(currency.getSuffixSymbol());
            existing.setSpaceBetweenAmountAndSymbol(currency.getSpaceBetweenAmountAndSymbol());
            existing.setLanguageId(currency.getLanguageId());
            existing.setUserId(currency.getUserId());

            return currencyRepository.save(existing);
        } else {
            return currencyRepository.save(currency);
        }
    }

    /**
     * Sync currencies from Tally (bulk operation)
     */
    @Transactional
    public void syncCurrenciesFromTally(List<Currency> currencies) {
        for (Currency currency : currencies) {
            upsertCurrency(currency);
        }
    }
}
