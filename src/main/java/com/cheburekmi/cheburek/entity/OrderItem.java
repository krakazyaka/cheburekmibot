package com.cheburekmi.cheburek.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    @Column(name = "menu_item_id", nullable = false)
    private String menuItemId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "is_xl", nullable = false)
    private Boolean isXL = false;
}