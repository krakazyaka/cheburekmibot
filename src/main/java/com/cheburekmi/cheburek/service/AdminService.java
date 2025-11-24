package com.cheburekmi.cheburek.service;

import com.cheburekmi.cheburek.entity.Order;
import com.cheburekmi.cheburek.entity.OrderStatus;
import com.cheburekmi.cheburek.entity.User;
import com.cheburekmi.cheburek.repository.OrderRepository;
import com.cheburekmi.cheburek.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Transactional
    public Order moveOrderStatus(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        
        OrderStatus currentStatus = order.getStatus();
        OrderStatus nextStatus = getNextStatus(currentStatus);
        
        if (nextStatus == OrderStatus.DONE) {
            userRepository.findById(order.getUserId())
                    .orElseThrow(() -> new RuntimeException("Cannot complete order: User not found with id: " + order.getUserId()));
        }
        
        order.setStatus(nextStatus);
        orderRepository.save(order);

        if (nextStatus == OrderStatus.DONE) {
            awardLoyaltyPoints(order);
        }

        return order;
    }

    @Transactional
    public User addLoyaltyPoints(String userCode, Long points) {
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new RuntimeException("User not found with code: " + userCode));
        
        Long currentPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0L;
        user.setLoyaltyPoints(currentPoints + points);
        
        return userRepository.save(user);
    }

    private OrderStatus getNextStatus(OrderStatus currentStatus) {
        return switch (currentStatus) {
            case CREATED -> OrderStatus.IN_PROGRESS;
            case IN_PROGRESS -> OrderStatus.READY;
            case READY -> OrderStatus.DONE;
            case DONE -> throw new RuntimeException("Order is already completed");
        };
    }

    private void awardLoyaltyPoints(Order order) {
        int chebureksCount = orderService.countChebureksInOrder(order);
        
        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + order.getUserId()));
        
        Long currentPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0L;
        user.setLoyaltyPoints(currentPoints + chebureksCount);
        
        userRepository.save(user);
    }
}
