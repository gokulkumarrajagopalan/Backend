package com.tally.service;

import com.tally.entity.Voucher;
import com.tally.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {
    
    @Autowired
    private VoucherRepository voucherRepository;
    
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }
    
    public Optional<Voucher> getVoucherById(Long id) {
        return voucherRepository.findById(id);
    }
    
    public Voucher createVoucher(Voucher voucher) {
        return voucherRepository.save(voucher);
    }
    
    public Voucher updateVoucher(Long id, Voucher voucher) {
        voucher.setId(id);
        return voucherRepository.save(voucher);
    }
    
    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }
    
    public List<Voucher> getVouchersByType(String type) {
        return voucherRepository.findByType(type);
    }
    
    public List<Voucher> getVouchersByDate(LocalDate date) {
        return voucherRepository.findByDate(date);
    }
    
    public Double getTotalVoucherAmount() {
        return voucherRepository.findAll().stream()
                .mapToDouble(Voucher::getAmount)
                .sum();
    }
}
