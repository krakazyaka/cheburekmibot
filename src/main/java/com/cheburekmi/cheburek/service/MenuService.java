package com.cheburekmi.cheburek.service;

import com.cheburekmi.cheburek.dto.MenuItemDto;
import com.cheburekmi.cheburek.entity.MenuItem;
import com.cheburekmi.cheburek.mapper.MenuItemMapper;
import com.cheburekmi.cheburek.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuService {
    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    public List<MenuItemDto> getMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAllByAvailableTrue();

        return menuItemMapper.toDtoList(menuItems);
    }
}
