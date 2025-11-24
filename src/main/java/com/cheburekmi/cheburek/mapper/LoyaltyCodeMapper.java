package com.cheburekmi.cheburek.mapper;

import com.cheburekmi.cheburek.dto.LoyaltyCodeDto;
import com.cheburekmi.cheburek.entity.LoyaltyCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoyaltyCodeMapper {
    LoyaltyCodeDto toDto(LoyaltyCode loyaltyCode);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    LoyaltyCode toEntity(LoyaltyCodeDto loyaltyCodeDto);
}