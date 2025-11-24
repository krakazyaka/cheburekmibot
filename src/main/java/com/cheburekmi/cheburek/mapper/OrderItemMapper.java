package com.cheburekmi.cheburek.mapper;

import com.cheburekmi.cheburek.dto.OrderItemDto;
import com.cheburekmi.cheburek.entity.OrderItem;
import com.cheburekmi.cheburek.entity.OrderItemAddon;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {OrderItemAddonMapper.class})
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);
    
    @Mapping(target = "id", ignore = true)
    OrderItem toEntity(OrderItemDto orderItemDto);
    
    @AfterMapping
    default void setOrderItemReference(@MappingTarget OrderItem orderItem) {
        if (orderItem.getAddons() != null) {
            for (OrderItemAddon addon : orderItem.getAddons()) {
                addon.setOrderItem(orderItem);
            }
        }
    }
}