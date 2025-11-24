package com.cheburekmi.cheburek.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
@Entity
@Table(name = "addons")
public class Addon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private String image;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @ManyToMany(mappedBy = "addons")
    private Set<MenuItem> menuItems;
}
