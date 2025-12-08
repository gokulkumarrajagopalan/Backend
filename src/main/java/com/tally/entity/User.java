package com.tally.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_email_licence", columnNames = {"email", "licence_no"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String email;
    
    @Column(name = "licence_no", nullable = false)
    private Long licenceNo;
    
    @Column(nullable = false)
    private String password;
    
    private String fullName;
    
    private String role;
    
    @Column(columnDefinition = "boolean default true")
    private boolean enabled;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "device_token", length = 500)
    private String deviceToken;
    
    @Column(name = "device_login_at")
    private LocalDateTime deviceLoginAt;
}
