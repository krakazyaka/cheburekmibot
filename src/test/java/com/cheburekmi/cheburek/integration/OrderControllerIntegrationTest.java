package com.cheburekmi.cheburek.integration;

import com.cheburekmi.cheburek.dto.OrderDto;
import com.cheburekmi.cheburek.dto.OrderItemDto;
import com.cheburekmi.cheburek.entity.MenuCategory;
import com.cheburekmi.cheburek.entity.MenuItem;
import com.cheburekmi.cheburek.entity.Order;
import com.cheburekmi.cheburek.entity.User;
import com.cheburekmi.cheburek.repository.MenuItemRepository;
import com.cheburekmi.cheburek.repository.OrderRepository;
import com.cheburekmi.cheburek.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private MenuItem testMenuItem;
    private String testUserToken;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        menuItemRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setTelegramId("123456789");
        testUser.setUserCode("TEST");
        testUser.setLoyaltyPoints(0L);
        testUser.setIsAdmin(false);
        testUser = userRepository.save(testUser);

        testMenuItem = new MenuItem();
        testMenuItem.setId(1L);
        testMenuItem.setName("Test Cheburek");
        testMenuItem.setCategory(MenuCategory.CHEBUR);
        testMenuItem.setPrice(BigDecimal.valueOf(150));
        testMenuItem.setAvailable(true);
        testMenuItem.setDescription("Test description");
        testMenuItem.setImage("test.jpg");
        testMenuItem = menuItemRepository.save(testMenuItem);

        testUserToken = jwtService.generateToken(testUser.getTelegramId(), testUser.getIsAdmin());
    }

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        OrderDto orderDto = new OrderDto();
        orderDto.setNotes("Test order");

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setMenuItemId(testMenuItem.getId().toString());
        itemDto.setName("Test Item");
        itemDto.setQuantity(2);
        itemDto.setIsXL(false);
        itemDto.setPrice(BigDecimal.valueOf(150));

        orderDto.setOrderItems(List.of(itemDto));
        orderDto.setTotal(BigDecimal.valueOf(150));

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk());

        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getUserId()).isEqualTo(testUser.getId());
        assertThat(orders.get(0).getNotes()).isEqualTo("Test order");
    }

    @Test
    void shouldGetMyOrdersSuccessfully() throws Exception {
        Order order = new Order();
        order.setUserId(testUser.getId());
        order.setTotal(BigDecimal.valueOf(300));
        order.setNotes("Existing order");
        orderRepository.save(order);

        mockMvc.perform(get("/api/orders/my-orders")
                        .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].notes").value("Existing order"));
    }

    @Test
    void shouldRejectOrderWithoutAuthentication() throws Exception {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderItems(new ArrayList<>());
        orderDto.setTotal(BigDecimal.valueOf(150));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldOnlyShowOwnOrders() throws Exception {
        User otherUser = new User();
        otherUser.setTelegramId("987654321");
        otherUser.setUserCode("OTH1");
        otherUser.setLoyaltyPoints(0L);
        otherUser.setIsAdmin(false);
        otherUser = userRepository.save(otherUser);

        Order otherOrder = new Order();
        otherOrder.setUserId(otherUser.getId());
        otherOrder.setTotal(BigDecimal.valueOf(500));
        otherOrder.setNotes("Other user order");
        orderRepository.save(otherOrder);

        Order myOrder = new Order();
        myOrder.setUserId(testUser.getId());
        myOrder.setTotal(BigDecimal.valueOf(300));
        myOrder.setNotes("My order");
        orderRepository.save(myOrder);

        mockMvc.perform(get("/api/orders/my-orders")
                        .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].notes").value("My order"));
    }
}
