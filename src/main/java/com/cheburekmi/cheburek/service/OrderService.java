package com.cheburekmi.cheburek.service;

import com.cheburekmi.cheburek.dto.OrderDto;
import com.cheburekmi.cheburek.entity.Order;
import com.cheburekmi.cheburek.entity.OrderStatus;
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
        orderEntity.setStatus(OrderStatus.CREATED);

        orderRepository.save(orderEntity);
    }

    public List<OrderDto> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId, Pageable.ofSize(10));

        return orderMapper.toDtoList(orders);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    public int countChebureksInOrder(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return 0;
        }
        return order.getOrderItems().stream()
                .mapToInt(orderItem -> orderItem.getQuantity())
                .sum();
    }
}
