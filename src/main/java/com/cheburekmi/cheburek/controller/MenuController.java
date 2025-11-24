package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.dto.MenuItemDto;
import com.cheburekmi.cheburek.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("api/menu")
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    List<MenuItemDto> getMenuItems() {
        log.info("Got request to get all menu items");
        return menuService.getMenuItems();
    }
}
