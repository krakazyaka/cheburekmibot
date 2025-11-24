package com.cheburekmi.cheburek.service;

import com.cheburekmi.cheburek.dto.OrderDto;
import com.cheburekmi.cheburek.entity.Order;
import com.cheburekmi.cheburek.mapper.OrderMapper;
import com.cheburekmi.cheburek.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public void createOrder(OrderDto order) {
        Order orderEntity = orderMapper.toEntity(order);

        orderRepository.save(orderEntity);
    }

    public List<OrderDto> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId, Pageable.ofSize(10));

        return orderMapper.toDtoList(orders);
    }
}
