package com.cheburekmi.cheburek.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String telegramId;
    private String userCode;
    private Long loyaltyPoints;
    private Boolean isAdmin;
}
