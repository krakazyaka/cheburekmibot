package com.cheburekmi.cheburek.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal tax;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}