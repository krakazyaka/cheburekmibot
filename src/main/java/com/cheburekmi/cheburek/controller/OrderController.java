package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.dto.OrderDto;
import com.cheburekmi.cheburek.entity.Order;
import com.cheburekmi.cheburek.entity.User;
import com.cheburekmi.cheburek.mapper.OrderMapper;
import com.cheburekmi.cheburek.repository.OrderRepository;
import com.cheburekmi.cheburek.repository.UserRepository;
import com.cheburekmi.cheburek.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> createOrder(
            @RequestBody OrderDto order,
            Authentication authentication) {
        log.info("Got createOrder request: {}", order);
        
        String authenticatedTelegramId = authentication.getName();
        Optional<User> user = userRepository.findByTelegramId(authenticatedTelegramId);
        
        if (user.isEmpty()) {
            log.warn("User with telegramId {} not found", authenticatedTelegramId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        order.setUserId(user.get().getId());
        
        orderService.createOrder(order);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderDto>> getMyOrders(Authentication authentication) {
        log.info("Got getMyOrders request");
        
        String authenticatedTelegramId = authentication.getName();
        Optional<User> user = userRepository.findByTelegramId(authenticatedTelegramId);
        
        if (user.isEmpty()) {
            log.warn("User with telegramId {} not found", authenticatedTelegramId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return ResponseEntity.ok(orderService.getOrdersByUserId(user.get().getId()));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateStatusRequest request) {
        log.info("Got updateOrderStatus request for order {} to status {}", orderId, request.status);
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        Order order = orderOpt.get();
        order.setStatus(request.status);
        Order savedOrder = orderRepository.save(order);
        
        return ResponseEntity.ok(orderMapper.toDto(savedOrder));
    }

    public record UpdateStatusRequest(String status) {}
}
