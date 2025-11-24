package com.cheburekmi.cheburek.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemAddonDto {
    private Long id;
    private Long addonId;
    private String name;
    private BigDecimal price;
}
