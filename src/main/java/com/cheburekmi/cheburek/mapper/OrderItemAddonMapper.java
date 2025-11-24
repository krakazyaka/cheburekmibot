package com.cheburekmi.cheburek.mapper;

import com.cheburekmi.cheburek.dto.OrderItemAddonDto;
import com.cheburekmi.cheburek.entity.OrderItemAddon;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemAddonMapper {
    OrderItemAddonDto toDto(OrderItemAddon orderItemAddon);
    OrderItemAddon toEntity(OrderItemAddonDto orderItemAddonDto);
    List<OrderItemAddonDto> toDtoList(List<OrderItemAddon> orderItemAddons);
}
