package com.tradingtrends.user.presentation.controller;


import com.tradingtrends.user.application.dto.UserResponse;
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

    @GetMapping("/{user_id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("user_id") String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // 회원가입 API
    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest userRequest) {
        User user = userService.registerUser(userRequest);
        return ResponseEntity.ok(new UserResponse(user.getUserId(), user.getPassword(),user.getEmail(), user.getUsername(), user.getRole().toString()));
    }

    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyUser(@RequestParam("user_id") String userId) {
        return ResponseEntity.ok(userService.verifyUser(userId));
    }
}