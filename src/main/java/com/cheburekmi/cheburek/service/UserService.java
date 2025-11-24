package com.cheburekmi.cheburek.service;

import com.cheburekmi.cheburek.dto.UserDto;
import com.cheburekmi.cheburek.entity.User;
import com.cheburekmi.cheburek.mapper.UserMapper;
import com.cheburekmi.cheburek.repository.UserRepository;
import com.cheburekmi.cheburek.util.CodeGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CodeGenerationService codeGenerationService;

    public UserDto getOrCreateUser(String telegramId) {

        Optional<User> user = userRepository.findByTelegramId(telegramId);
        if (user.isPresent()) {
            return userMapper.toDto(user.get());
        } else  {
            User newUser = createUser(telegramId);
            userRepository.save(newUser);
            return userMapper.toDto(newUser);
        }
    }

    private User createUser(String telegramId) {
        User entity = new User();
        entity.setTelegramId(telegramId);
        entity.setUserCode(codeGenerationService.generateUniqueUserCode());
        entity.setLoyaltyPoints(0L);

        return entity;
    }

}
