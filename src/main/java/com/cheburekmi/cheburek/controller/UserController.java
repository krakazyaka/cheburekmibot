package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.dto.LoginResponse;
import com.cheburekmi.cheburek.dto.UserDto;
import com.cheburekmi.cheburek.entity.User;
import com.cheburekmi.cheburek.mapper.UserMapper;
import com.cheburekmi.cheburek.service.JwtService;
import com.cheburekmi.cheburek.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @PostMapping("/find-or-create")
    public ResponseEntity<LoginResponse> findOrCreateUser(@RequestBody FindOrCreateUserRequest request) {
        log.info("Got request to find or create user with telegramId {}", request.telegramId);
        
        User user = userService.getOrCreateUserEntity(request.telegramId);
        
        String token = jwtService.generateToken(user.getTelegramId(), user.getIsAdmin());
        UserDto userDto = userMapper.toDto(user);
        
        return ResponseEntity.ok(new LoginResponse(token, userDto));
    }

    @GetMapping("/{telegramId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> getUser(
            @PathVariable("telegramId") String telegramId,
            Authentication authentication) {
        log.info("Got request to get user with telegramId {}", telegramId);
        
        String authenticatedTelegramId = authentication.getName();
        if (!authenticatedTelegramId.equals(telegramId)) {
            log.warn("User {} tried to access data of user {}", authenticatedTelegramId, telegramId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(userService.getOrCreateUser(telegramId));
    }

    public record FindOrCreateUserRequest(
        String telegramId,
        String firstName,
        String lastName,
        String username
    ) {}
}
