package com.tally.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledgers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ledger {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "group_id")
    private Long groupId;
    
    private Double balance;
    
    private String address;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
