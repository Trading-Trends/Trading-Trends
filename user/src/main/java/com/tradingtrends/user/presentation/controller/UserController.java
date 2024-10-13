package com.tradingtrends.user.presentation.controller;


import com.tradingtrends.user.application.dto.UserResponseDto;
import com.tradingtrends.user.application.service.UserService;
import com.tradingtrends.user.domain.model.User;
import com.tradingtrends.user.presentation.request.UserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 API
    @PostMapping
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequest userRequest) {
        User user = userService.registerUser(userRequest);
        return ResponseEntity.ok(new UserResponseDto(user.getUserId(), user.getUsername(), user.getPassword(),user.getEmail(), user.getRole().toString()));
    }

    // userId로 user 검증
    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyUser(@RequestParam("user_id") Long userId) {
        return ResponseEntity.ok(userService.verifyUser(userId));
    }

    // userId로 user 조회 및 응답
    @GetMapping("/id/{user_id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // username으로 조회
    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

}