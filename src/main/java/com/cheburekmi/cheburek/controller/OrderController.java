package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.dto.OrderDto;
import com.cheburekmi.cheburek.entity.Order;
import com.cheburekmi.cheburek.entity.User;
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
@PreAuthorize("hasRole('USER')")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping
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
        
        order.setUserId(Long.parseLong(user.get().getId()));
        
        orderService.createOrder(order);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDto>> getMyOrders(Authentication authentication) {
        log.info("Got getMyOrders request");
        
        String authenticatedTelegramId = authentication.getName();
        Optional<User> user = userRepository.findByTelegramId(authenticatedTelegramId);
        
        if (user.isEmpty()) {
            log.warn("User with telegramId {} not found", authenticatedTelegramId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return ResponseEntity.ok(orderService.getOrdersByUserId(Long.parseLong(user.get().getId())));
    }
}
