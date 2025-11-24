package com.cheburekmi.cheburek.mapper;

import com.cheburekmi.cheburek.dto.MenuItemDto;
import com.cheburekmi.cheburek.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    MenuItemDto toDto(MenuItem menuItem);
    @Mapping(target = "id", ignore = true)
    MenuItem toEntity(MenuItemDto menuItemDto);
    
    List<MenuItemDto> toDtoList(List<MenuItem> menuItems);
    List<MenuItem> toEntityList(List<MenuItemDto> menuItemDtos);
}