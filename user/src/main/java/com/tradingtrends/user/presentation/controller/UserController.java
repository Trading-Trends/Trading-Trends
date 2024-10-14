package com.tradingtrends.user.presentation.controller;


import com.tradingtrends.user.application.dto.UserResponseDto;
import com.tradingtrends.user.application.service.UserService;
import com.tradingtrends.user.presentation.request.DeleteRequestDto;
import com.tradingtrends.user.presentation.request.UserRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 API
    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody UserRequestDto userRequestDto) {
        userService.registerUser(userRequestDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 사용자 정보 수정 API
    @PatchMapping("/{user_id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable("user_id") Long userId, @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto updatedUser = userService.updateUser(userId, userRequestDto);
        return ResponseEntity.ok(updatedUser);
    }

    // 사용자 삭제 API
    @DeleteMapping("/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable("user_id") Long userId, @RequestBody DeleteRequestDto dto) {
        boolean isDeleted = userService.deleteUser(userId, dto.getPassword());
        if (isDeleted) {
            return ResponseEntity.ok("사용자 삭제 성공");
        } else {
            return ResponseEntity.status(400).body("비밀번호가 일치하지 않거나 사용자가 존재하지 않습니다.");
        }
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

}