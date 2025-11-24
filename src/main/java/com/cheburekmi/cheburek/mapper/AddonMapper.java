package com.cheburekmi.cheburek.mapper;

import com.cheburekmi.cheburek.dto.AddonDto;
import com.cheburekmi.cheburek.entity.Addon;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddonMapper {
    AddonDto toDto(Addon addon);
    Addon toEntity(AddonDto addonDto);
    List<AddonDto> toDtoList(List<Addon> addons);
}
