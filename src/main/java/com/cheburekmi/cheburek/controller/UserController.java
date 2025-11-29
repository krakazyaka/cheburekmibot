package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.dto.UserDto;
import com.cheburekmi.cheburek.mapper.UserMapper;
import com.cheburekmi.cheburek.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping("/{telegramId}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable("telegramId") String telegramId) {
        log.info("Got request to get user with telegramId {}", telegramId);

        return ResponseEntity.ok(userService.getOrCreateUser(telegramId));
    }

}
