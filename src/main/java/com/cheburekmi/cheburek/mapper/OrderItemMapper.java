package com.cheburekmi.cheburek.mapper;

import com.cheburekmi.cheburek.dto.OrderItemDto;
import com.cheburekmi.cheburek.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);
    @Mapping(target = "id", ignore = true)
    OrderItem toEntity(OrderItemDto orderItemDto);
}