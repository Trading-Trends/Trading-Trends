package com.tradingtrends.user.presentation.controller;

import com.tradingtrends.user.application.dto.UserDetailsDto;
import com.tradingtrends.user.application.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
public class UserFeignController {

    private final UserService userService;

    public UserFeignController(UserService userService) {
        this.userService = userService;
    }

    // userId로 user 조회 및 응답
    @GetMapping("/{user_id}/details")
    public ResponseEntity<UserDetailsDto> getUserDetailsById(@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(userService.getUserDetailsById(userId));
    }

    // username으로 user 조회 및 응답
    @GetMapping
    public ResponseEntity<UserDetailsDto> getUserDetailsByUsername(@RequestParam("username") String username) {
        return ResponseEntity.ok(userService.getUserDetailsByUsername(username));
    }

    // userId로 user 검증
    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyUser(@RequestParam("user_id") Long userId) {
        return ResponseEntity.ok(userService.verifyUser(userId));
    }

}
