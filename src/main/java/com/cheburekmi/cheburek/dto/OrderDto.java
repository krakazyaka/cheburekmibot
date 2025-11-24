package com.cheburekmi.cheburek.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private String userId;
    private String status;
    private BigDecimal total;
    private String notes;
    private List<OrderItemDto> orderItems;
    private LocalDateTime createdAt;
}