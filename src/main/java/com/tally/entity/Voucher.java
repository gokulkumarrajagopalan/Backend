package com.tally.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String type; // Payment, Receipt, Journal
    
    private LocalDate date;
    
    @Column(name = "account_id")
    private Long accountId;
    
    private Double amount;
    
    private String remarks;
    
    private String account;
}
