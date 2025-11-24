package com.cheburekmi.cheburek.mapper;

import com.cheburekmi.cheburek.dto.OrderDto;
import com.cheburekmi.cheburek.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Order toEntity(OrderDto orderDto);

    List<OrderDto> toDtoList(List<Order> orders);
}