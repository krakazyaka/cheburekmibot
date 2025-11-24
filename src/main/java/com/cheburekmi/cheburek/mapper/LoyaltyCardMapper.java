package com.cheburekmi.cheburek.mapper;

import com.cheburekmi.cheburek.dto.LoyaltyCardDto;
import com.cheburekmi.cheburek.entity.LoyaltyCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoyaltyCardMapper {
    LoyaltyCardDto toDto(LoyaltyCard loyaltyCard);
    @Mapping(target = "id", ignore = true)
    LoyaltyCard toEntity(LoyaltyCardDto loyaltyCardDto);
}