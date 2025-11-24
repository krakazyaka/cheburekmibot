package com.cheburekmi.cheburek.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "loyalty_cards")
public class LoyaltyCard {
    @Id
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;
    
    @Column(nullable = false)
    private Integer current = 0;
    
    @Column(nullable = false)
    private Integer target = 10;
    
    @Column(name = "free_available", nullable = false)
    private Integer freeAvailable = 0;
}