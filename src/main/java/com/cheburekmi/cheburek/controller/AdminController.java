package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.dto.OrderDto;
import com.cheburekmi.cheburek.entity.Order;
import com.cheburekmi.cheburek.entity.User;
import com.cheburekmi.cheburek.mapper.OrderMapper;
import com.cheburekmi.cheburek.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final OrderMapper orderMapper;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<Order> orders = adminService.getAllOrders();
        return ResponseEntity.ok(orderMapper.toDtoList(orders));
    }

    @PostMapping("/orders/{orderId}/move")
    public ResponseEntity<OrderDto> moveOrderStatus(@PathVariable Long orderId) {
        Order order = adminService.moveOrderStatus(orderId);
        return ResponseEntity.ok(orderMapper.toDto(order));
    }

    @PostMapping("/users/{userCode}/loyalty-points")
    public ResponseEntity<UserLoyaltyResponse> addLoyaltyPoints(
            @PathVariable String userCode,
            @RequestBody LoyaltyPointsRequest request) {
        User user = adminService.addLoyaltyPoints(userCode, request.points());
        return ResponseEntity.ok(new UserLoyaltyResponse(
                user.getUserCode(),
                user.getLoyaltyPoints()
        ));
    }

    public record LoyaltyPointsRequest(Long points) {}
    
    public record UserLoyaltyResponse(String userCode, Long loyaltyPoints) {}
}
