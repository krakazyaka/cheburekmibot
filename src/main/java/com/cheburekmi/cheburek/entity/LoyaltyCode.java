package com.cheburekmi.cheburek.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "loyalty_codes")
public class LoyaltyCode {
    @Id
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;
    
    @Column(nullable = false)
    private Boolean used = false;
    
    @Column(name = "used_by")
    private String usedBy;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}