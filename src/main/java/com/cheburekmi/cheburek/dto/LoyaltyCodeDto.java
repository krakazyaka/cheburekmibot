package com.cheburekmi.cheburek.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoyaltyCodeDto {
    private Long id;
    private String code;
    private Boolean used;
    private String usedBy;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}