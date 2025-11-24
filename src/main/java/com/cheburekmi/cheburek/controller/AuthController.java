package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.dto.LoginRequest;
import com.cheburekmi.cheburek.dto.LoginResponse;
import com.cheburekmi.cheburek.dto.UserDto;
import com.cheburekmi.cheburek.entity.User;
import com.cheburekmi.cheburek.mapper.UserMapper;
import com.cheburekmi.cheburek.service.JwtService;
import com.cheburekmi.cheburek.service.TelegramAuthService;
import com.cheburekmi.cheburek.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TelegramAuthService telegramAuthService;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (!telegramAuthService.validateInitData(loginRequest.getInitData())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Telegram authentication data");
        }

        String telegramId = telegramAuthService.extractTelegramId(loginRequest.getInitData());
        if (telegramId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Could not extract Telegram ID");
        }

        User user = userService.getOrCreateUserEntity(telegramId);
        
        String token = jwtService.generateToken(user.getTelegramId(), user.getIsAdmin());
        UserDto userDto = userMapper.toDto(user);

        return ResponseEntity.ok(new LoginResponse(token, userDto));
    }
}
