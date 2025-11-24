package com.cheburekmi.cheburek.mapper;

import com.cheburekmi.cheburek.dto.UserDto;
import com.cheburekmi.cheburek.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    @Mapping(target = "id", ignore = true)
    User toEntity(UserDto userDto);
    
    List<UserDto> toDtoList(List<User> users);
    List<User> toEntityList(List<UserDto> userDtos);
}