package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.dto.OrderDto;
import com.cheburekmi.cheburek.entity.LoyaltyCard;
import com.cheburekmi.cheburek.entity.Order;
import com.cheburekmi.cheburek.entity.User;
import com.cheburekmi.cheburek.mapper.OrderMapper;
import com.cheburekmi.cheburek.repository.LoyaltyCardRepository;
import com.cheburekmi.cheburek.repository.UserRepository;
import com.cheburekmi.cheburek.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final LoyaltyCardRepository loyaltyCardRepository;

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

    @PostMapping("/offline-purchase")
    public ResponseEntity<OfflinePurchaseResponse> recordOfflinePurchase(@RequestBody OfflinePurchaseRequest request) {
        Optional<User> userOpt = userRepository.findByUserCode(request.userCode.toUpperCase());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        User user = userOpt.get();
        LoyaltyCard card = loyaltyCardRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    LoyaltyCard newCard = new LoyaltyCard();
                    newCard.setUserId(user.getId());
                    newCard.setCurrent(0);
                    newCard.setTarget(10);
                    newCard.setFreeAvailable(0);
                    return newCard;
                });
        
        int newCurrent = card.getCurrent() + request.cheburekCount;
        int newFreeAvailable = card.getFreeAvailable();
        
        while (newCurrent >= card.getTarget()) {
            newCurrent -= card.getTarget();
            newFreeAvailable++;
        }
        
        card.setCurrent(newCurrent);
        card.setFreeAvailable(newFreeAvailable);
        loyaltyCardRepository.save(card);
        
        return ResponseEntity.ok(new OfflinePurchaseResponse(
                user.getUserCode(),
                card.getCurrent(),
                card.getFreeAvailable()
        ));
    }

    public record LoyaltyPointsRequest(Long points) {}
    
    public record UserLoyaltyResponse(String userCode, Long loyaltyPoints) {}
    
    public record OfflinePurchaseRequest(String userCode, Integer cheburekCount) {}
    
    public record OfflinePurchaseResponse(String userCode, Integer current, Integer freeAvailable) {}
}
