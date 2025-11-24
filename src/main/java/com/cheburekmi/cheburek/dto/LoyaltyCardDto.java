package com.cheburekmi.cheburek.dto;

import lombok.Data;

@Data
public class LoyaltyCardDto {
    private Long id;
    private String userId;
    private Integer current;
    private Integer target;
    private Integer freeAvailable;
}