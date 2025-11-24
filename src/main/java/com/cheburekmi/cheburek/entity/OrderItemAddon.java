package com.cheburekmi.cheburek.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_item_addons")
public class OrderItemAddon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;
    
    @Column(name = "addon_id", nullable = false)
    private Long addonId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
}
