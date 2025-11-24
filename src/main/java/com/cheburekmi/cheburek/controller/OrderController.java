package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.dto.OrderDto;
import com.cheburekmi.cheburek.entity.Order;
import com.cheburekmi.cheburek.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderDto order) {
        log.info("Got createOrder request: {}", order);
        orderService.createOrder(order);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public List<OrderDto> getAllOrders(@PathVariable Long userId) {
        log.info("Got getAllOrders request: {}", userId);
        return orderService.getOrdersByUserId(userId);
    }
}
