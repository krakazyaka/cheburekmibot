package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.dto.UserDto;
import com.cheburekmi.cheburek.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final UserService userService;

    @GetMapping("/{telegramId}")
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
}
