package com.cheburekmi.cheburek.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "menu_items")
public class MenuItem {
    @Id
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MenuCategory category;
    
    @Column(nullable = false)
    private String image;
    
    @Column(name = "has_xl", nullable = false)
    private Boolean hasXL = false;
    
    @Column(nullable = false)
    private Boolean available = true;
}