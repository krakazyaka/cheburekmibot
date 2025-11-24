package com.cheburekmi.cheburek.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;
    
    @Column(name = "telegram_id", nullable = false, unique = true)
    private String telegramId;
    
    @Column(name = "user_code", nullable = false, unique = true, length = 4)
    private String userCode;

    @Column(name = "loyalty_points")
    private Long loyaltyPoints;
    
    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;
}