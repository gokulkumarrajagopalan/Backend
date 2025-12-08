package com.tally.service;

import com.tally.entity.Party;
import com.tally.repository.PartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PartyService {
    @Autowired
    private PartyRepository partyRepository;

    public Party createParty(Party party) {
        if (party.getName() == null || party.getName().isEmpty()) {
            throw new RuntimeException("Party name is required");
        }
        if (party.getPartyType() == null || party.getPartyType().isEmpty()) {
            throw new RuntimeException("Party type is required");
        }
        party.setCurrentBalance(0.0);
        party.setActive(true);
        party.setCreatedAt(LocalDateTime.now());
        party.setUpdatedAt(LocalDateTime.now());
        return partyRepository.save(party);
    }

    public Optional<Party> getPartyById(Long id) {
        return partyRepository.findById(id);
    }

    public List<Party> getAllParties() {
        return partyRepository.findAll();
    }

    public List<Party> getPartiesByCompany(Long companyId) {
        return partyRepository.findByCompanyId(companyId);
    }

    public List<Party> getPartiesByCompanyAndType(Long companyId, String partyType) {
        return partyRepository.findByCompanyIdAndPartyType(companyId, partyType);
    }

    public List<Party> getActivePartiesForCompany(Long companyId) {
        return partyRepository.findByCompanyIdAndActiveTrue(companyId);
    }

    public List<Party> getPartiesByCity(Long companyId, String city) {
        return partyRepository.findByCompanyIdAndCity(companyId, city);
    }

    public Party updateParty(Long id, Party party) {
        Optional<Party> existing = partyRepository.findById(id);
        if (existing.isPresent()) {
            Party p = existing.get();
            if (party.getName() != null) p.setName(party.getName());
            if (party.getContactPerson() != null) p.setContactPerson(party.getContactPerson());
            if (party.getEmail() != null) p.setEmail(party.getEmail());
            if (party.getPhone() != null) p.setPhone(party.getPhone());
            if (party.getAddress() != null) p.setAddress(party.getAddress());
            if (party.getCity() != null) p.setCity(party.getCity());
            if (party.getState() != null) p.setState(party.getState());
            if (party.getPinCode() != null) p.setPinCode(party.getPinCode());
            if (party.getGstNumber() != null) p.setGstNumber(party.getGstNumber());
            if (party.getPanNumber() != null) p.setPanNumber(party.getPanNumber());
            if (party.getCreditLimit() != null) p.setCreditLimit(party.getCreditLimit());
            if (party.getPaymentTerms() != null) p.setPaymentTerms(party.getPaymentTerms());
            if (party.getActive() != null) p.setActive(party.getActive());
            p.setUpdatedAt(LocalDateTime.now());
            return partyRepository.save(p);
        }
        throw new RuntimeException("Party not found");
    }

    public void updatePartyBalance(Long partyId, Double amount, String type) {
        Optional<Party> party = partyRepository.findById(partyId);
        if (party.isPresent()) {
            Party p = party.get();
            Double currentBalance = p.getCurrentBalance();
            
            if ("debit".equalsIgnoreCase(type)) {
                p.setCurrentBalance(currentBalance + amount);
            } else if ("credit".equalsIgnoreCase(type)) {
                p.setCurrentBalance(currentBalance - amount);
            }
            p.setUpdatedAt(LocalDateTime.now());
            partyRepository.save(p);
        }
    }

    public void deleteParty(Long id) {
        partyRepository.deleteById(id);
    }

    public Optional<Party> findByName(Long companyId, String name) {
        return partyRepository.findByCompanyIdAndName(companyId, name);
    }
}
