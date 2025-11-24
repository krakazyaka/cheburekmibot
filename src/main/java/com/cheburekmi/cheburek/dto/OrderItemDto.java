package com.cheburekmi.cheburek.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Long id;
    private String orderId;
    private String menuItemId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Boolean isXL;
}