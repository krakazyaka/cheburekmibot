package com.cheburekmi.cheburek.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MenuItemDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String image;
    private Boolean hasXL;
    private Boolean available;
}