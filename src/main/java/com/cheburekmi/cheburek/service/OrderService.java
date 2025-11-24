package com.cheburekmi.cheburek.service;

import com.cheburekmi.cheburek.dto.OrderDto;
import com.cheburekmi.cheburek.dto.OrderItemDto;
import com.cheburekmi.cheburek.dto.OrderItemAddonDto;
import com.cheburekmi.cheburek.entity.*;
import com.cheburekmi.cheburek.mapper.OrderMapper;
import com.cheburekmi.cheburek.repository.AddonRepository;
import com.cheburekmi.cheburek.repository.MenuItemRepository;
import com.cheburekmi.cheburek.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final MenuItemRepository menuItemRepository;
    private final AddonRepository addonRepository;

    public void createOrder(OrderDto order) {
        validateAndRecalculateOrder(order);
        
        Order orderEntity = orderMapper.toEntity(order);
        orderEntity.setStatus(OrderStatus.CREATED);

        orderRepository.save(orderEntity);
    }
    
    private void validateAndRecalculateOrder(OrderDto order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (OrderItemDto itemDto : order.getOrderItems()) {
            if (itemDto.getMenuItemId() == null) {
                throw new IllegalArgumentException("Menu item ID is required");
            }
            
            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
            
            MenuItem menuItem = menuItemRepository.findById(Integer.parseInt(itemDto.getMenuItemId()))
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + itemDto.getMenuItemId()));
            
            if (!menuItem.getAvailable()) {
                throw new IllegalArgumentException("Menu item is not available: " + menuItem.getName());
            }
            
            BigDecimal itemPrice = menuItem.getPrice();
            if (Boolean.TRUE.equals(itemDto.getIsXL())) {
                if (!menuItem.getHasXL()) {
                    throw new IllegalArgumentException("XL option not available for: " + menuItem.getName());
                }
                itemPrice = itemPrice.multiply(BigDecimal.valueOf(2));
            }
            
            itemDto.setName(menuItem.getName());
            itemDto.setPrice(itemPrice);
            
            BigDecimal addonTotal = BigDecimal.ZERO;
            if (itemDto.getAddons() != null && !itemDto.getAddons().isEmpty()) {
                for (OrderItemAddonDto addonDto : itemDto.getAddons()) {
                    Addon addon = addonRepository.findById(addonDto.getAddonId())
                        .orElseThrow(() -> new IllegalArgumentException("Addon not found: " + addonDto.getAddonId()));
                    
                    if (!addon.getAvailable()) {
                        throw new IllegalArgumentException("Addon is not available: " + addon.getName());
                    }
                    
                    boolean addonValidForMenuItem = menuItem.getAddons() != null && 
                        menuItem.getAddons().stream()
                            .anyMatch(a -> a.getId().equals(addon.getId()));
                    
                    if (!addonValidForMenuItem) {
                        throw new IllegalArgumentException("Addon " + addon.getName() + " is not available for " + menuItem.getName());
                    }
                    
                    addonDto.setName(addon.getName());
                    addonDto.setPrice(addon.getPrice());
                    addonTotal = addonTotal.add(addon.getPrice());
                }
            }
            
            BigDecimal lineTotal = itemPrice.add(addonTotal).multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }
        
        BigDecimal tax = BigDecimal.ZERO;
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotal(subtotal.add(tax));
    }

    public List<OrderDto> getOrdersByUserId(String userId) {
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
